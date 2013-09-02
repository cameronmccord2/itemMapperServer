package com.ItemMapper.dao;

import java.util.List;

import com.ItemMapper.model.DoesUserExist;
import com.ItemMapper.model.Item;
import com.ItemMapper.model.SecurityQuestion;
import com.ItemMapper.model.Token;
import com.ItemMapper.model.User;

public abstract interface ItemMapperDAO {

	Item putItem(Item item, String token);

	List<Item> getAllItemsForUser(String token);

	List<Item> removeItemFromUser(String itemId, String token);

	Token loginUser(String email, String password);

	User getUserInfo(String token);

	DoesUserExist doesUserExist(String email);

	Integer logoutUser(String token);

	List<SecurityQuestion> getSecurityQuestions(String email);

	User putUser(User newUser);

	
//	MissionaryLanguage getMissionaryLanguageByLanguageId(String languageid);
//	
//	MissionaryLanguage getMissionaryLanguageByName(String name);
//	
//	MissionaryLanguage getMissionaryLanguageByTallLanguageId(String talllanguageid);
//	
//	List<MissionaryLanguage> getAllMissionaryLanguages();
//	
//	TallTask getTallTaskByLanguageId(String languageid);
//	
//	TallTask getTallTaskByName(String name);
//	
//	TallTask getTallTaskByTallLanguageId(String talllanguageid);
//
//	List<SeniorAudioFlagsToClient> getSeniorMissionaryAudioByLanguageAndCategory(String language, String category);
//
//	Integer putNewSeniorAudioFlag(String language, String category, SeniorAudioFlags body);
//
//	Integer disableFlagGroup(String groupId);
//
//	Integer enableFlagGroup(String groupId);
//
//	List<SeniorAudioFlagsToClient> getSeniorMissionaryAudioLanguageHistory(String language);
}
