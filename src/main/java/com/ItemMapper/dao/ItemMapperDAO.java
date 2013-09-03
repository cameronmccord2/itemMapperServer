package com.ItemMapper.dao;

import java.util.List;

import com.ItemMapper.model.DoesUserExist;
import com.ItemMapper.model.ItemListElement;
import com.ItemMapper.model.ItemLocation;
import com.ItemMapper.model.NewItem;
import com.ItemMapper.model.SecurityQuestion;
import com.ItemMapper.model.Token;
import com.ItemMapper.model.User;

public abstract interface ItemMapperDAO {

	ItemListElement putItem(NewItem item, String token);

	List<ItemListElement> getAllItemsForUser(String token);

	List<ItemListElement> removeItemFromUser(String itemId, String token);

	Token loginUser(String email, String password);

	User getUserInfo(String token);

	DoesUserExist doesUserExist(String email);

	Integer logoutUser(String token);

	List<SecurityQuestion> getSecurityQuestions(String email);

	User putUser(User newUser);

	List<ItemLocation> getLocationsForItem(String token, Integer id);

	ItemListElement putNewLocationForExistingItem(ItemLocation location);

	
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
