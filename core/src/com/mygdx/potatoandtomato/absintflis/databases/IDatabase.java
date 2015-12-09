package com.mygdx.potatoandtomato.absintflis.databases;

import com.mygdx.potatoandtomato.helpers.assets.Profile;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public interface IDatabase {

    void getTestTableCount(DatabaseListener<Integer> listener);

    void loginAnonymous(DatabaseListener<Profile> listener);

    void getProfileByUserId(String userId, DatabaseListener<Profile> listener);

    void getProfileByFacebookUserId(String facebookUserId, DatabaseListener<Profile> listener);

    void updateProfile(Profile profile);

    void createUserByUserId(String userId, DatabaseListener<Profile> listener);


}
