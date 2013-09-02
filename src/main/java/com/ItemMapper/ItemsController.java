package com.ItemMapper;

import java.util.List;
import java.util.zip.DataFormatException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ItemMapper.dao.ItemMapperDAO;
import com.ItemMapper.model.Item;

@Controller
public class ItemsController {
	
	private @Autowired HttpServletRequest request;
	
	@Inject
	@Named("itemMapperDAO")
	private ItemMapperDAO itemMapperDAO;
	
	@RequestMapping(method=RequestMethod.GET, value="/items")
	public @ResponseBody List<Item> getAllItemsForUser(@RequestHeader("Authorization") String token){
		return itemMapperDAO.getAllItemsForUser(token);
	}
	
	@RequestMapping(method=RequestMethod.DELETE, value="/items/{itemId}")
	public @ResponseBody List<Item> removeItemFromUser(@PathVariable String itemId, @RequestHeader("Authorization") String token){
		return itemMapperDAO.removeItemFromUser(itemId, token);
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/items")
	public @ResponseBody Item putItem(@RequestBody Item item, @RequestHeader("Authorization") String token){
		return itemMapperDAO.putItem(item, token);
	}
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
	

}