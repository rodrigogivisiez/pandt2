package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.downloader.IDownloader;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.sounds.ISounds;
import com.mygdx.potatoandtomato.absintflis.uploader.IUploader;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.controls.Chat;
import com.mygdx.potatoandtomato.helpers.controls.Confirm;
import com.mygdx.potatoandtomato.helpers.controls.Notification;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.mygdx.potatoandtomato.helpers.utils.Threadings;
import com.mygdx.potatoandtomato.models.ChatMessage;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.BroadcastEvent;
import com.potatoandtomato.common.Broadcaster;
import com.potatoandtomato.common.IPTGame;

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
	ISounds _sounds;

	@Override
	public void create () {

		_game = this;
		_assets = new Assets();
		_processors = new HashMap();
		Threadings.setMainTreadId();

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
				_chat = new Chat(_gamingKit, _texts, _assets, _batch, _game, _recorder, _uploader);
				_confirm = new Confirm(_batch, _game, _assets);
				_notification = new Notification(_batch, _assets, _game);
				_sounds = new Sounds(_assets);

				Preferences preferences = new Preferences();
				_services = new Services(_assets, _texts,
						preferences, new Profile(), new FirebaseDB(Terms.FIREBASE_URL),
						new Shaders(), _gamingKit, _downloader, _chat,
						new Socials(preferences), new GCMSender(), _confirm, _notification,
						_recorder, _uploader, _sounds, new VersionControl());
				_screen = new PTScreen(_game, _services);

				setScreen(_screen);


				_screen.toScene(SceneEnum.BOOT);
				}
		});
	}

	@Override
	public void dispose() {
		super.dispose();
		Broadcaster.getInstance().broadcast(BroadcastEvent.REMOVE_APPS_ALIVE);
		Broadcaster.getInstance().broadcast(BroadcastEvent.DESTROY_ROOM);
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

		_confirm.render(Gdx.graphics.getDeltaTime());
		_chat.render(Gdx.graphics.getDeltaTime());
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

}
