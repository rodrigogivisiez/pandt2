package com.mygdx.potatoandtomato;

import com.badlogic.gdx.Game;
import com.mygdx.potatoandtomato.absintflis.databases.DatabaseListener;
import com.mygdx.potatoandtomato.absintflis.databases.IDatabase;
import com.mygdx.potatoandtomato.absintflis.databases.SpecialDatabaseListener;
import com.mygdx.potatoandtomato.absintflis.gamingkit.ConnectionChangedListener;
import com.mygdx.potatoandtomato.enums.SceneEnum;
import com.mygdx.potatoandtomato.helpers.services.*;
import com.mygdx.potatoandtomato.models.Room;
import com.mygdx.potatoandtomato.models.Services;
import com.mygdx.potatoandtomato.models.Profile;

import java.util.ArrayList;

public class PTGame extends Game {

	Services _services;
	Textures _textures;
	Fonts _fonts;
	PTScreen _screen;
	Texts _texts;

	@Override
	public void create () {
		_services = new Services(new Textures(), new Fonts(), new Texts(),
									new Preferences(), new Profile(), new FirebaseDB(),
									new Shaders(), new Appwarp(), new Downloader());
		_screen = new PTScreen(_services);

		setScreen(_screen);

		_services.setDatabase(new IDatabase() {
			@Override
			public void getTestTableCount(DatabaseListener<Integer> listener) {

			}

			@Override
			public void loginAnonymous(DatabaseListener<Profile> listener) {

			}

			@Override
			public void getProfileByUserId(String userId, DatabaseListener<Profile> listener) {

			}

			@Override
			public void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener) {

			}

			@Override
			public void updateProfile(Profile profile) {

			}

			@Override
			public void createUserByUserId(String userId, DatabaseListener<Profile> listener) {

			}

			@Override
			public void getAllGames(DatabaseListener<ArrayList<com.mygdx.potatoandtomato.models.Game>> listener) {

			}

			@Override
			public void saveRoom(Room room, DatabaseListener<String> listener) {

			}

			@Override
			public void changeSlotIndex(Room room, Profile user, Integer newIndex, DatabaseListener<String> listener) {

			}

			@Override
			public void monitorRoomById(String id, DatabaseListener<Room> listener) {

			}

			@Override
			public void getRoomById(String id, DatabaseListener<Room> listener) {

			}

			@Override
			public void monitorAllRooms(ArrayList<Room> rooms, SpecialDatabaseListener<ArrayList<Room>, Room> listener) {

			}

			@Override
			public void notifyRoomChanged(Room room) {

			}

			@Override
			public void removeUserFromRoomOnDisconnect(Room room, Profile user, DatabaseListener<String> listener) {

			}

			@Override
			public void offline() {

			}

			@Override
			public void online() {

			}
		});

		Room r = new Room();
		com.mygdx.potatoandtomato.models.Game g = new com.mygdx.potatoandtomato.models.Game();
		g.setVersion("1");
		g.setAbbr("covered_chess");
		g.setName("Covered Chess");
		g.setGameUrl("http://www.potato-and-tomato.com/covered_chess/core.jar");
		g.setAssetUrl("http://www.potato-and-tomato.com/covered_chess/assets.zip");
		g.setIconUrl("http://www.potato-and-tomato.com/covered_chess/icon.png");
		g.setMaxPlayers("2");
		g.setMinPlayers("2");
		g.setTeamMaxPlayers("3");
		g.setTeamCount("2");
		g.setDescription("Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.");
		g.setTeamMinPlayers("1");
		r.setGame(g);
		r.setHost(_services.getProfile());
		_services.getProfile().setGameName("soulwraith");
		_services.getProfile().setUserId("123");
		_screen.toScene(SceneEnum.ROOM, r);


//		_services.getGamingKit().addListener(new ConnectionChangedListener() {
//			@Override
//			public void onChanged(Status st) {
//				if(st == Status.CONNECTED){
//					com.mygdx.potatoandtomato.models.Game g = new com.mygdx.potatoandtomato.models.Game();
//					g.setVersion("1");
//					g.setAbbr("covered_chess");
//					g.setGameUrl("http://www.potato-and-tomato.com/covered_chess/core.jar");
//					g.setAssetUrl("http://www.potato-and-tomato.com/covered_chess/assets.zip");
//					_screen.toScene(SceneEnum.PREREQUISITE, g, true);
//				}
//			}
//		});
//		_services.getGamingKit().connect("test");



	}



}
