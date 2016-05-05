package com.potatoandtomato.games;

import com.badlogic.gdx.assets.AssetManager;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.absints.GameEntrance;
import com.potatoandtomato.common.absints.PTAssetsManager;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.assets.*;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;
import com.potatoandtomato.games.screens.hints.HintsLogic;
import com.potatoandtomato.games.screens.main.ImageStorage;
import com.potatoandtomato.games.screens.main.MainLogic;
import com.potatoandtomato.games.screens.main.StageImagesLogic;
import com.potatoandtomato.games.screens.review.ReviewLogic;
import com.potatoandtomato.games.screens.scores.ScoresLogic;
import com.potatoandtomato.games.screens.stage_counter.StageCounterLogic;
import com.potatoandtomato.games.screens.time_bar.CastleLogic;
import com.potatoandtomato.games.screens.time_bar.KingLogic;
import com.potatoandtomato.games.screens.time_bar.KnightLogic;
import com.potatoandtomato.games.screens.time_bar.TimeLogic;
import com.potatoandtomato.games.screens.user_counters.UserCountersLogic;
import com.potatoandtomato.games.services.Database;
import com.potatoandtomato.games.services.RoomMsgHandler;
import com.potatoandtomato.games.services.SoundsWrapper;
import com.potatoandtomato.games.services.Texts;

/**
 * Created by SiongLeng on 14/7/2015.
 */
public class Entrance extends GameEntrance {

    Services _services;
    MyAssets _assets;
    GameCoordinator _coordinator;
    MainLogic _mainLogic;
    TimeLogic _timeLogic;
    UserCountersLogic _userCountersLogic;
    HintsLogic _hintsLogic;
    ImageStorage _imageStorage;
    ReviewLogic _reviewLogic;
    StageCounterLogic _stageCounterLogic;
    KingLogic _kingLogic;
    KnightLogic _knightLogic;
    CastleLogic _castleLogic;
    ScoresLogic _scoresLogic;
    GameModel _gameModel;
    StageImagesLogic _stageImagesLogic;


    public Entrance(final GameCoordinator gameCoordinator) {
        super(gameCoordinator);
        this._coordinator = gameCoordinator;
        getGameCoordinator().setLandscape();

        initAssets();

        _assets.loadAsync(new Runnable() {
            @Override
            public void run() {

                _gameModel = new GameModel();

                _kingLogic = new KingLogic(_gameModel, getServices());
                _castleLogic = new CastleLogic(_gameModel, getServices(), gameCoordinator);
                _knightLogic = new KnightLogic(_gameModel, getServices(), gameCoordinator);

                _timeLogic = new TimeLogic(getServices(), _coordinator, _kingLogic, _castleLogic, _knightLogic, _gameModel);
                _userCountersLogic = new UserCountersLogic(_gameModel, getServices(), _coordinator);
                _hintsLogic = new HintsLogic(_gameModel, getServices(), _coordinator);
                _imageStorage = new ImageStorage(getServices(), _coordinator);
                _reviewLogic = new ReviewLogic(_gameModel, getServices(), _coordinator);
                _stageCounterLogic = new StageCounterLogic(getServices(), _coordinator, _gameModel);
                _scoresLogic = new ScoresLogic(_coordinator, getServices(), _gameModel, _knightLogic, _castleLogic, _hintsLogic);
                _stageImagesLogic = new StageImagesLogic(_coordinator, getServices(), _gameModel);

                _mainLogic = new MainLogic(getGameCoordinator(), getServices(), _timeLogic, _hintsLogic, _reviewLogic,
                        _userCountersLogic, _stageCounterLogic, _scoresLogic, _imageStorage, _gameModel,
                        _stageImagesLogic);


                getGameCoordinator().finishLoading();
            }
        });

    }

    @Override
    public void init() {
        _mainLogic.init();
        _scoresLogic.refreshAllScores();
        getGameCoordinator().getGame().setScreen((_mainLogic.getMainScreen()));
    }

    @Override
    public void onContinue() {
        _mainLogic.onContinue();
        _scoresLogic.refreshAllScores();
        getGameCoordinator().getGame().setScreen((_mainLogic.getMainScreen()));
    }

    @Override
    public void dispose() {
        if(_assets != null) _assets.dispose();
        if(_services != null) _services.dispose();
        if(_mainLogic != null) _mainLogic.dispose();
        if(_stageImagesLogic != null) _stageImagesLogic.dispose();
        if(_timeLogic != null) _timeLogic.dispose();
        if(_userCountersLogic != null) _userCountersLogic.dispose();
        if(_imageStorage != null) _imageStorage.dispose();
        if(_scoresLogic != null) _scoresLogic.dispose();
    }

    private void initAssets(){
        PTAssetsManager manager = _coordinator.getPTAssetManager(true);
        Fonts fonts = new Fonts(manager);
        Patches patches = new Patches();
        Sounds sounds = new Sounds(manager);
        Textures textures = new Textures(manager, "pack.atlas");
        Animations animations = new Animations(manager);

        _assets = new MyAssets(manager, fonts, animations, sounds, patches, textures);
    }

    public Services getServices() {
        if(_services == null){
            Database database = new Database(_coordinator.getFirebase());
            Texts texts = new Texts();
            _services = new Services(_assets, new SoundsWrapper(_assets, _coordinator), database,
                    texts, new RoomMsgHandler(_coordinator));
        }
        return _services;
    }

    public void setServices(Services _services) {
        this._services = _services;
    }
}
