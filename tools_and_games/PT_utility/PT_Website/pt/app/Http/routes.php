<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the controller to call when that URI is requested.
|
*/


Route::get('/games', array('as' => 'games', 'uses' => 'MainController@PAGE_games'));

Route::get('/faq', array('as' => 'faq', 'uses' => 'MainController@PAGE_faq'));

Route::get('/terms', array('as' => 'terms', 'uses' => 'MainController@PAGE_terms'));

Route::get('/privacy', array('as' => 'privacy', 'uses' => 'MainController@PAGE_privacy'));


Route::get('/support', array('as' => 'support', 'uses' => 'MainController@PAGE_support'));

Route::post('/leaderboard', array('as' => 'leaderboard', 'uses' => 'MainController@CONTROL_leaderboard'));

Route::post('/userDetails', array('as' => 'userDetails', 'uses' => 'MainController@CONTROL_userDetails'));

Route::post('/submitSupport', array('as' => 'submitSupport', 'uses' => 'MainController@submit_support'));


Route::get('/', array('as' => 'root', 'uses' => 'MainController@PAGE_root'));