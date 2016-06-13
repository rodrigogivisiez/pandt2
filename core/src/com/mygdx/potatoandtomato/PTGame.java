package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.uploader.IUploader;
import com.mygdx.potatoandtomato.assets.*;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.services.*;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.utils.ForAppwarpTesting;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.absints.IDownloader;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.Broadcaster;
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
	IDownloader _downloader;
	SoundsPlayer _soundsPlayer;
	Broadcaster _broadcaster;
	Preferences _preferences;
	RestfulApi _restfulApi;
	Tutorials _tutorials;
	ConnectionWatcher _connectionWatcher;
	Profile _profile;
	PTAssetsManager _monitoringPTAssetsManager;
	ArrayList<Runnable> _onResumeRunnables;

	public PTGame(Broadcaster broadcaster) {
		_broadcaster = broadcaster;
		_onResumeRunnables = new ArrayList();
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
		_assets.loadAsync(new Runnable() {
			@Override
			public void run() {

				_profile = new Profile();
				_batch = new SpriteBatch();
				_gamingKit = new Appwarp();
				_texts = new Texts();
				_soundsPlayer = new SoundsPlayer(_assets, _broadcaster);
				_recorder = new Recorder(_soundsPlayer, _broadcaster);
				_downloader = new Downloader();

				_chat = new Chat(_broadcaster, _gamingKit, _texts, _assets,
										_soundsPlayer, _recorder, _batch, _game, _preferences);
				_confirm = new Confirm(_batch, _game, _assets, _broadcaster);
				_notification = new Notification(_batch, _assets, _game, _broadcaster);
				_tutorials = new Tutorials(_game, _batch, _soundsPlayer, _assets, _broadcaster);
				_restfulApi = new RestfulApi();
				_connectionWatcher = new ConnectionWatcher(_gamingKit, _batch, _assets,  _broadcaster, _confirm, _texts, _game);

				_services = new Services(_assets, _texts,
						_preferences, _profile, new FirebaseDB(Terms.FIREBASE_URL()),
						new Shaders(), _gamingKit, _downloader, _chat,
						new Socials(_preferences, _broadcaster), new GCMSender(), _confirm, _notification,
						_recorder, _soundsPlayer, new VersionControl(), _broadcaster,
						_tutorials, _restfulApi, _connectionWatcher);
				_screen = new PTScreen(_game, _services);
				_connectionWatcher.setPtScreen(_screen);
				setScreen(_screen);

				_screen.toScene(SceneEnum.BOOT);

				Logs.show("COINNN");
			}
		});
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		if(_chat != null) _chat.resize(width, height);
		if(_notification != null) _notification.resize(width, height);
		if(_confirm != null) _confirm.resize(width, height);
		if(_tutorials != null) _tutorials.resize(width, height);
		if(_connectionWatcher != null) _connectionWatcher.resize(width, height);
	}

	@Override
	public void dispose() {
		super.dispose();
		_broadcaster.broadcast(BroadcastEvent.DESTROY_ROOM);
		if(_services != null && _services.getAssets() != null) _services.getAssets().dispose();
	}


	public SpriteBatch getSpriteBatch() {
		return _batch;
	}

	public void setSpriteBatch(SpriteBatch _batch) {
		this._batch = _batch;
	}


	@Override
	public void resume() {
		super.resume();
		for(Runnable runnable : _onResumeRunnables){
			runnable.run();
		}
	}

	@Override
	public void render() {
		super.render();

		if(_monitoringPTAssetsManager != null && !_monitoringPTAssetsManager.isFinishLoading() && _monitoringPTAssetsManager.update()) {
			_monitoringPTAssetsManager.setFinishLoading(true);
			_monitoringPTAssetsManager = null;
		}

		if(Gdx.input.justTouched())
		{
			if(_chat != null) _chat.screenTouched(Gdx.input.getX(), Gdx.input.getY());
		}

		if(_chat != null) _chat.render(Gdx.graphics.getDeltaTime());
		if(_tutorials != null) _tutorials.render(Gdx.graphics.getDeltaTime());
		if(_confirm != null) _confirm.render(Gdx.graphics.getDeltaTime());
		if(_connectionWatcher != null) _connectionWatcher.render(Gdx.graphics.getDeltaTime());
		if(_notification != null) _notification.render(Gdx.graphics.getDeltaTime());
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
		PTAssetsManager manager = new PTAssetsManager(new InternalFileHandleResolver(), this);
		Animations animations = new Animations(manager);
		Patches patches = new Patches(manager);
		Sounds sounds = new Sounds(manager);
		Textures textures = new Textures(manager, "ui_pack.atlas");
		Fonts fonts = new Fonts(manager);

		_assets = new Assets(manager, fonts, animations, sounds, patches, textures);
	}

	@Override
	public void monitorPTAssetManager(PTAssetsManager ptAssetsManager){
		_monitoringPTAssetsManager = ptAssetsManager;
	}

	@Override
	public void addOnResumeRunnable(Runnable toRun) {
		_onResumeRunnables.add(toRun);
	}

	@Override
	public void removeOnResumeRunnable(Runnable toRun) {
		_onResumeRunnables.remove(toRun);
	}

}
