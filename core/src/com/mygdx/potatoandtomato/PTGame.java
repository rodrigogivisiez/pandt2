package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.potatoandtomato.absintflis.gamingkit.GamingKit;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.controls.Chat;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.helpers.utils.Logs;
import com.mygdx.potatoandtomato.helpers.utils.Terms;
import com.mygdx.potatoandtomato.models.Profile;
import com.mygdx.potatoandtomato.models.Services;
import com.potatoandtomato.common.IPTGame;

public class PTGame extends Game implements IPTGame {

	Services _services;
	Assets _assets;
	PTScreen _screen;
	Texts _texts;
	SpriteBatch _batch;
	Chat _chat;
	GamingKit _gamingKit;
	PTGame _game;
	Array<InputProcessor> _processors;

	@Override
	public void create () {
		_game = this;
		_assets = new Assets();
		_processors = new Array<InputProcessor>();

		//run when assets done loading
		_assets.loadBasic(new Runnable() {
			@Override
			public void run() {

				_batch = new SpriteBatch();
				_gamingKit = new Appwarp();
				_texts = new Texts();
				_chat = new Chat(_gamingKit, _texts, _assets, _batch, _game);
				Preferences preferences = new Preferences();
				_services = new Services(_assets, _texts,
						preferences, new Profile(), new FirebaseDB(Terms.FIREBASE_URL),
						new Shaders(), _gamingKit, new Downloader(), _chat,
						new Socials(preferences), new GCMSender());
				_screen = new PTScreen(_game, _services);

				setScreen(_screen);
				_screen.toScene(SceneEnum.BOOT);
			}
		});



		Logs.startLogFps();


	}

	public SpriteBatch getSpriteBatch() {
		return _batch;
	}

	@Override
	public void render() {
		super.render();
		_chat.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void addInputProcessor(InputProcessor processor, int index){
		_processors.insert(index, processor);
		setInputProcessors();
	}

	@Override
	public void addInputProcessor(InputProcessor processor) {
		_processors.add(processor);
		setInputProcessors();
	}

	@Override
	public void removeInputProcessor(InputProcessor processor) {
		_processors.removeValue(processor, false);
		setInputProcessors();
	}

	private void setInputProcessors(){
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.setProcessors(_processors);
		Gdx.input.setInputProcessor(multiplexer);
	}

}
