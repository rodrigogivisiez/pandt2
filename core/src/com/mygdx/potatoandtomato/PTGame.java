package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.absintflis.mocks.MockModel;
import com.mygdx.potatoandtomato.assets.*;
import com.mygdx.potatoandtomato.enums.ConfirmIdentifier;
import com.mygdx.potatoandtomato.enums.LeaderboardType;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.models.*;
import com.mygdx.potatoandtomato.scenes.leaderboard_scene.EndGameLeaderBoardLogic;
import com.mygdx.potatoandtomato.services.*;
import com.mygdx.potatoandtomato.statics.Global;
import com.mygdx.potatoandtomato.statics.Terms;
import com.mygdx.potatoandtomato.utils.Logs;
import com.potatoandtomato.common.absints.IDownloader;
import com.potatoandtomato.common.absints.IPTGame;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.absints.TutorialPartListener;
import com.potatoandtomato.common.assets.Assets;
import com.potatoandtomato.common.broadcaster.BroadcastEvent;
import com.potatoandtomato.common.broadcaster.Broadcaster;
import com.potatoandtomato.common.enums.GestureType;
import com.potatoandtomato.common.enums.SpeechActionType;
import com.potatoandtomato.common.enums.Status;
import com.potatoandtomato.common.models.*;
import com.potatoandtomato.common.utils.Downloader;
import com.potatoandtomato.common.utils.Pair;
import com.potatoandtomato.common.utils.Threadings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PTGame extends Game implements IPTGame {

	SplashScreen _splashScreen;
	Services _services;
	MyAssets _assets;
	PTScreen _screen;
	Texts _texts;
	SpriteBatch _batch;
	Chat _chat;
	GamingKit _gamingKit;
	PTGame _game;
	Confirm _confirm;
	Notification _notification;
	Recorder _recorder;
	IDownloader _downloader;
	SoundsPlayer _soundsPlayer;
	Broadcaster _broadcaster;
	Preferences _preferences;
	RestfulApi _restfulApi;
	Tutorials _tutorials;
	Loggings _loggings;
	ConnectionWatcher _connectionWatcher;
	Coins _coins;
	DataCaches _dataCaches;
	Profile _profile;
	IDatabase _database;
	PTAssetsManager _monitoringPTAssetsManager;
	AutoJoiner _autoJoiner;
	ArrayList<Runnable> _onResumeRunnables;

	HashMap<InputProcessor, Integer> _processors;
	ArrayList<InputProcessor> _externalProcessors;

	public PTGame(Broadcaster broadcaster) {
		this(broadcaster, null);
	}

	public PTGame(Broadcaster broadcaster, String autoJoinRoomId) {
		_broadcaster = broadcaster;
		_onResumeRunnables = new ArrayList();
		_autoJoiner = new AutoJoiner(autoJoinRoomId);
	}

	@Override
	public void create () {
		_game = this;
		_preferences = new Preferences();
		_processors = new HashMap();
		_externalProcessors = new ArrayList();
		Threadings.setMainTreadId();
		Global.init(_preferences);
		_splashScreen = new SplashScreen();
		setScreen(_splashScreen);
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
				_recorder = new Recorder(_assets, _soundsPlayer, _broadcaster);
				_downloader = new Downloader();
				_database = new FirebaseDB(Terms.FIREBASE_URL());
				_loggings = new Loggings(_batch, _assets, _game, _broadcaster);
				Logs.setLoggings(_loggings);

				_chat = new Chat(_broadcaster, _gamingKit, _texts, _assets,
										_soundsPlayer, _recorder, _batch, _game, _preferences);
				_confirm = new Confirm(_batch, _game, _assets, _broadcaster);
				_notification = new Notification(_batch, _assets, _game, _broadcaster, _soundsPlayer);
				_tutorials = new Tutorials(_game, _batch, _soundsPlayer, _assets, _broadcaster, _preferences, _texts);
				_restfulApi = new RestfulApi();
				_connectionWatcher = new ConnectionWatcher(_gamingKit, _broadcaster, _confirm, _texts, _profile);
				_dataCaches = new DataCaches(_database, _restfulApi, _profile, _broadcaster);
				_coins = new Coins(_broadcaster, _assets, _soundsPlayer, _texts,
						_game, _batch, _profile, _database, _gamingKit, _restfulApi, _confirm, _connectionWatcher,
						_dataCaches);
				_autoJoiner.init(_confirm, _texts);

				_services = new Services(_assets, _texts,
						_preferences, _profile, _database,
						new Shaders(), _gamingKit, _downloader, _chat,
						new Socials(_preferences, _broadcaster), new GCMSender(), _confirm, _notification,
						_recorder, _soundsPlayer, new VersionControl(), _broadcaster,
						_tutorials, _restfulApi, _connectionWatcher, _coins, _dataCaches, _autoJoiner);
				_screen = new PTScreen(_game, _services);
				_connectionWatcher.setPtScreen(_screen);
				_coins.setPtScreen(_screen);

				_splashScreen.close(new Runnable() {
					@Override
					public void run() {
						setScreen(_screen);
						_screen.toScene(SceneEnum.BOOT);
					}
				});

			}
		});
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		if(_chat != null) _chat.resize(width, height);
		if(_coins != null) _coins.resize(width, height);
		if(_notification != null) _notification.resize(width, height);
		if(_confirm != null) _confirm.resize(width, height);
		if(_tutorials != null) _tutorials.resize(width, height);
		if(_loggings != null) _loggings.resize(width, height);
	}

	@Override
	public void dispose() {
		super.dispose();
		if(_screen != null) _screen.dispose();
		_broadcaster.broadcast(BroadcastEvent.DESTROY_ROOM);
		if(_services != null && _services.getAssets() != null) _services.getAssets().dispose();
		if(_services != null && _services.getAssets() != null) _services.getGamingKit().dispose();
		if(_services != null && _services.getDatabase() != null) _services.getDatabase().offline();
		if(_services != null && _services.getDataCaches() != null) _services.getDataCaches().dispose();
		if(_splashScreen != null) _splashScreen.dispose();
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

		if(_coins != null) _coins.render(Gdx.graphics.getDeltaTime());
		if(_chat != null) _chat.render(Gdx.graphics.getDeltaTime());
		if(_tutorials != null) _tutorials.render(Gdx.graphics.getDeltaTime());
		if(_confirm != null) _confirm.render(Gdx.graphics.getDeltaTime());
		if(_notification != null) _notification.render(Gdx.graphics.getDeltaTime());
		if(_loggings != null) _loggings.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void addInputProcessor(InputProcessor processor, int priority, boolean external) {
		if(!_processors.containsKey(processor)) {
			if(external) _externalProcessors.add(processor);
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

	@Override
	public void removeAllExternalProcessors() {
		for(InputProcessor inputProcessor : _externalProcessors){
			_processors.remove(inputProcessor);
		}
		_externalProcessors.clear();
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

		_assets = new MyAssets(manager, fonts, animations, sounds, patches, textures);
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
