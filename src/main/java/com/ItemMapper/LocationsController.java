package com.ItemMapper;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.ItemMapper.dao.ItemMapperDAO;
import com.ItemMapper.model.ItemListElement;
import com.ItemMapper.model.ItemLocation;

@Controller
public class LocationsController {
	
	private @Autowired HttpServletRequest request;
	
	@Inject
	@Named("itemMapperDAO")
	private ItemMapperDAO itemMapperDAO;
	
	@RequestMapping(method=RequestMethod.GET, value="/locations")
	public @ResponseBody List<ItemLocation> getLocationsForItem(@RequestHeader("Authorization") String token, @RequestParam Integer itemId){
		return itemMapperDAO.getLocationsForItem(token, itemId);
	}
	
	@RequestMapping(method=RequestMethod.GET, value="/healthCheck")
	public @ResponseBody Integer getLocationsForItem(){
		return 1;
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/locations")
	public @ResponseBody ItemListElement putNewLocationForExistingItem(@RequestBody ItemLocation location){
		return itemMapperDAO.putNewLocationForExistingItem(location);
	}
}
