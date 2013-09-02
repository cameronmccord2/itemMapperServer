package com.ItemMapper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ItemMapper.dao.ItemMapperDAO;
import com.ItemMapper.model.DoesUserExist;
import com.ItemMapper.model.Token;
import com.ItemMapper.model.User;
import com.ItemMapper.model.SecurityQuestion;

@Controller
public class UserController {
	
	private @Autowired HttpServletRequest request;
	
	@Inject
	@Named("itemMapperDAO")
	private ItemMapperDAO itemMapperDAO;
	
	@RequestMapping(method=RequestMethod.GET, value="/users/login")
	public @ResponseBody Token loginUser(@RequestParam String email, @RequestParam String password){
		return itemMapperDAO.loginUser(email, password);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/users")
	public @ResponseBody User getUserInfo(@RequestHeader("Authorization") String token){
		return itemMapperDAO.getUserInfo(token);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/users/exists")
	public @ResponseBody DoesUserExist doesUserExist(@RequestParam String email){
		return itemMapperDAO.doesUserExist(email);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/users/logout")
	public @ResponseBody Integer logoutUser(@RequestHeader("Authorization") String token){
		return itemMapperDAO.logoutUser(token);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/users/securityQuestion")
	public @ResponseBody List<SecurityQuestion> getSecurityQuestions(@RequestParam String email){
		return itemMapperDAO.getSecurityQuestions(email);
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/users")
	public @ResponseBody User putUser(@RequestBody User newUser){
		return itemMapperDAO.putUser(newUser);
	}
//	
//	@RequestMapping(method=RequestMethod.GET, value="/missionarylanguage/{languageid}/languageid")
//	public @ResponseBody MissionaryLanguage getMissionaryLanguageByLanguageId(@PathVariable String languageid){
//		return mmlDAO.getMissionaryLanguageByLanguageId(languageid);
//	}
//	
//	@RequestMapping(method=RequestMethod.GET, value="/missionarylanguage/{talllanguageid}/talllanguageid")
//	public @ResponseBody MissionaryLanguage getMissionaryLanguageByTallLanguageId(@PathVariable String talllanguageid){
//		return mmlDAO.getMissionaryLanguageByTallLanguageId(talllanguageid);
//	}
//	
//	@RequestMapping(method=RequestMethod.GET, value="/missionarylanguage/{name}/name")
//	public @ResponseBody MissionaryLanguage getMissionaryLanguageByName(@PathVariable String name){
//		return mmlDAO.getMissionaryLanguageByName(name);
//	}
//	
//	@RequestMapping(method=RequestMethod.GET, value="/talltask/{languageid}/languageid")
//	public @ResponseBody TallTask getTallTaskByLanguageId(@PathVariable String languageid){
//		return mmlDAO.getTallTaskByLanguageId(languageid);
//	}
//	
//	@RequestMapping(method=RequestMethod.GET, value="/talltask/{name}/name")
//	public @ResponseBody TallTask getTallTaskByName(@PathVariable String name){
//		return mmlDAO.getTallTaskByName(name);
//	}
//	
//	@RequestMapping(method=RequestMethod.GET, value="/talltask/{talllanguageid}/talllanguageid")
//	public @ResponseBody TallTask getTallTaskByTallLanguageId(@PathVariable String talllanguageid){
//		return mmlDAO.getTallTaskByTallLanguageId(talllanguageid);
//	}
//	
//	@RequestMapping(method=RequestMethod.GET, value="/seniormissionaryaudio/history/{language}")
//	public @ResponseBody List<SeniorAudioFlagsToClient> getSeniorMissionaryAudioLanguageHistory(@PathVariable String language){
//		return mmlDAO.getSeniorMissionaryAudioLanguageHistory(language);
//	}
//	
//	@RequestMapping(method=RequestMethod.POST, value="/seniormissionaryaudio/disable/{groupId}")
//	public @ResponseBody Integer disableFlagGroup(@PathVariable String groupId){
//		return mmlDAO.disableFlagGroup(groupId);
//	}
//	
//	@RequestMapping(method=RequestMethod.POST, value="/seniormissionaryaudio/enable/{groupId}")
//	public @ResponseBody Integer enableFlagGroup(@PathVariable String groupId){
//		return mmlDAO.enableFlagGroup(groupId);
//	}
//	
//	@RequestMapping(method=RequestMethod.GET, value="/seniormissionaryaudio/{language}/{category}")
//	public @ResponseBody List<SeniorAudioFlagsToClient> getSeniorMissionaryAudioByLanguageAndCategory(@PathVariable String language, @PathVariable String category){
//		return mmlDAO.getSeniorMissionaryAudioByLanguageAndCategory(language, category);
//	}
//	
//	@RequestMapping(method=RequestMethod.PUT, value="/seniormissionaryaudio/{language}/{category}")
//	public @ResponseBody Integer putNewSeniorAudioFlag(@PathVariable String language, @PathVariable String category, @RequestBody SeniorAudioFlags body){
//		return mmlDAO.putNewSeniorAudioFlag(language, category, body);
//	}
}