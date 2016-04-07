package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
import com.mygdx.potatoandtomato.assets.*;
import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.EndGameLeaderBoardLogic;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.uploader.IUploader;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.services.Chat;
import com.mygdx.potatoandtomato.helpers.services.Confirm;
import com.mygdx.potatoandtomato.helpers.services.Notification;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.potatoandtomato.common.absints.IDownloader;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.models.EndGameResult;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.models.ScoreDetails;
import com.potatoandtomato.common.utils.Downloader;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PTGame extends Game implements IPTGame {

	Services _services;
	Assets _assets;
	PTScreen _screen;
	Texts _texts;
	SpriteBatch _batch;
	Chat _chat;
	GamingKit _gamingKit;
	PTGame _game;
	HashMap<InputProcessor, Integer> _processors;
	Confirm _confirm;
	Notification _notification;
	Recorder _recorder;
	IUploader _uploader;
	IDownloader _downloader;
	SoundsPlayer _soundsWrapper;
	Broadcaster _broadcaster;
	Preferences _preferences;
	Tutorials _tutorials;

	public PTGame(Broadcaster broadcaster) {
		_broadcaster = broadcaster;

	}

	@Override
	public void create () {

		_game = this;
		_preferences = new Preferences();
		_processors = new HashMap();
		Threadings.setMainTreadId();
		Global.init(_preferences);
		initiateAssets();

		//run when assets done loading
		_assets.loadBasic(new Runnable() {
			@Override
			public void run() {

				_batch = new SpriteBatch();
				_gamingKit = new Appwarp();
				_texts = new Texts();
				_recorder = new Recorder();
				_downloader = new Downloader();
				_uploader = new App42Uploader(_downloader);
				_soundsWrapper = new SoundsPlayer(_assets, _broadcaster);
				_chat = new Chat(_gamingKit, _texts, _assets, _batch, _game, _recorder, _uploader, _soundsWrapper, _broadcaster);
				_confirm = new Confirm(_batch, _game, _assets, _broadcaster);
				_notification = new Notification(_batch, _assets, _game, _broadcaster);
				_tutorials = new Tutorials(_game, _batch, _soundsWrapper, _assets, _broadcaster);

				_services = new Services(_assets, _texts,
						_preferences, new Profile(), new FirebaseDB(Terms.FIREBASE_URL),
						new Shaders(), _gamingKit, _downloader, _chat,
						new Socials(_preferences, _broadcaster), new GCMSender(), _confirm, _notification,
						_recorder, _uploader, _soundsWrapper, new VersionControl(), _broadcaster,
						_tutorials);
				_screen = new PTScreen(_game, _services);

				setScreen(_screen);

				_screen.toScene(SceneEnum.BOOT);

			}
		});
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		_chat.resize(width, height);
		_notification.resize(width, height);
		_confirm.resize(width, height);
		_tutorials.resize(width, height);
	}

	@Override
	public void dispose() {
		super.dispose();
		_broadcaster.broadcast(BroadcastEvent.DESTROY_ROOM);
		_services.getAssets().dispose();
	}


	public SpriteBatch getSpriteBatch() {
		return _batch;
	}

	public void setSpriteBatch(SpriteBatch _batch) {
		this._batch = _batch;
	}

	@Override
	public void render() {
		super.render();
		if(Gdx.input.justTouched())
		{
			_chat.screenTouched(Gdx.input.getX(), Gdx.input.getY());
		}

		_chat.render(Gdx.graphics.getDeltaTime());
		_tutorials.render(Gdx.graphics.getDeltaTime());
		_confirm.render(Gdx.graphics.getDeltaTime());
		_notification.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void addInputProcessor(InputProcessor processor, int priority){
		if(!_processors.containsKey(processor)) {
			_processors.put(processor, priority);
			setInputProcessors();
		}
	}

	@Override
	public void addInputProcessor(InputProcessor processor) {
		if(!_processors.containsKey(processor)){
			_processors.put(processor, 0);
			setInputProcessors();
		}
	}

	@Override
	public void removeInputProcessor(InputProcessor processor) {
		_processors.remove(processor);
		setInputProcessors();
	}

	private void setInputProcessors(){
		Array<InputProcessor> inputProcessors = new Array();
		for (Map.Entry<InputProcessor, Integer> entry : _processors.entrySet()) {
			InputProcessor key = entry.getKey();
			Integer priority = entry.getValue();
			boolean inserted = false;
			for(int i = 0; i< inputProcessors.size; i++){
				if(priority > _processors.get(inputProcessors.get(i))){
					inputProcessors.insert(i, key);
					inserted = true;
					break;
				}
			}
			if(!inserted){
				inputProcessors.add(key);
			}
		}

		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.setProcessors(inputProcessors);
		Gdx.input.setInputProcessor(multiplexer);
	}

	private void initiateAssets(){
		AssetManager manager = new AssetManager(new InternalFileHandleResolver());
		Animations animations = new Animations(manager);
		Patches patches = new Patches();
		Sounds sounds = new Sounds(manager);
		Textures textures = new Textures(manager, "ui_pack.atlas");
		Fonts fonts = new Fonts(manager);

		_assets = new Assets(manager, fonts, animations, sounds, patches, textures);
	}



}
