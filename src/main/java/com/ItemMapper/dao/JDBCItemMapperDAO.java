package com.ItemMapper.dao;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.ItemMapper.model.DoesUserExist;
import com.ItemMapper.model.ItemListElement;
import com.ItemMapper.model.NewItem;
import com.ItemMapper.model.SecurityQuestion;
import com.ItemMapper.model.Token;
import com.ItemMapper.model.User;
import com.ItemMapper.model.ItemLocation;
import com.ItemMapper.model.ItemOwned;


public class JDBCItemMapperDAO extends NamedParameterJdbcDaoSupport implements ItemMapperDAO {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	private SecureRandom random = new SecureRandom();
	
	private enum StringTypes {
		ERROR_NOT_EMPTY, TRIM_EMPTY, ERROR_EMPTY, NOT_EMPTY, TRIM_NOT_EMPTY
	}
	
	private enum Status {
		activeToken(1),
		inactiveToken(2),
		activeSecurityQuestion(1),
		inactiveSecurityQuestion(2),
		activeItemOwned(1),
		inactiveItemOwned(2),
		activeUser(1),
		inactiveUser(2);
		
		private int value;
		Status(int status){
			this.value = status;
		}
	}
	
	private enum Type{
		tokenDevice(1),
		tokenWeb(2),
		userLocationGetter(1),
		userLocationSetter(2);
		
		private int value;
		Type(int type){
			this.value = type;
		}
	}
	
	private enum History{
		create(1);
		
		private int value;
		History(int value){
			this.value = value;
		}
	}
	
	private enum Fields {
		firstName(50, "firstName"),
		lastName(50, "lastName"),
		email(256, "email"),
		password(25, "password"),
		question(50, "question"),
		answer(50, "answer"),
		name(30, "name"),
		userComment(200, "userComment"),
		details(20, "details"),
		code(4000, "code"),
		longitude(20, "longitude"),
		latitude(20, "latitude"),
		token(24, "token");
		
		private int length;
		private String fieldName;
		Fields(int length, String fieldName){
			this.length = length;
			this.fieldName = fieldName;
		}
		
	}
	
	private Integer getUserIdForToken(String token){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("token", trimString(token, Fields.token.length, StringTypes.ERROR_NOT_EMPTY, Fields.token.fieldName));
		params.addValue("activeStatus", Status.activeToken.value);
		List<Token> tokens = getNamedParameterJdbcTemplate().query("SELECT userId FROM tokens WHERE status = :activeStatus AND token = :token", params, new BeanPropertyRowMapper<Token>(Token.class));
		if(tokens.size() == 1)
			return tokens.get(0).getUserId();
		else if(tokens.size() == 0)
			throw new RuntimeException("Token invalid, not found in user table");
		else
			throw new RuntimeException("more than one line found for the same token in getUserIdForToken");
	}
	
	private String generateToken(){
		while(true){
			MapSqlParameterSource params = new MapSqlParameterSource();
			String newToken = null;
			boolean keepGoing = true;
			int count = 0;
			while(keepGoing){
				newToken = new BigInteger(120, random).toString(32);
				if(newToken.length() == 24)
					keepGoing = false;
				count++;
				if(count == 1000)
					throw new RuntimeException("can generate a 24 digit token, tired 1000 times, last one was size: " + newToken.length() + " and was: " + newToken);
			}
			params.addValue("newToken", newToken);
			int i = getNamedParameterJdbcTemplate().query("SELECT id FROM tokens WHERE token = :newToken", params, new BeanPropertyRowMapper<Token>(Token.class)).size();
			if(i == 0)
				return newToken;
			else if(i > 1)
				throw new RuntimeException("token exists multiple times in tokens table already: " + newToken);
		}
	}
	
	private String trimString(String source, Integer maxLength, StringTypes type, String whatField){
		if(source.length() == 0){
			if(type == StringTypes.TRIM_NOT_EMPTY || type == StringTypes.NOT_EMPTY || type == StringTypes.ERROR_NOT_EMPTY)
				throw new RuntimeException("The field: " + whatField + " cannot be empty");
		}else if(source.length() > maxLength){
			if(type == StringTypes.ERROR_EMPTY || type == StringTypes.ERROR_NOT_EMPTY)
				throw new RuntimeException("The field: " + whatField + " is too long. It is: " + source.length() + ", should be: " + maxLength + " or less");
			return source.substring(0, maxLength);
		}
		return source;
	}
	
	private List<ItemLocation> getListOfItemsLocations(int itemId){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("itemId", itemId);
		return getNamedParameterJdbcTemplate().query("SELECT * FROM locations WHERE itemId = :itemId ORDER BY id desc", params, new BeanPropertyRowMapper<ItemLocation>(ItemLocation.class));
	}
	
	private boolean doesUserHaveItem(String token, Integer id){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("itemId", id);
		params.addValue("userId", getUserIdForToken(token));
		if(getNamedParameterJdbcTemplate().query("SELECT * FROM itemOwned WHERE itemId = :itemId AND userId = :userId", params, new BeanPropertyRowMapper<ItemOwned>(ItemOwned.class)).size() > 0)
		    return true;
		else
		    return false;
	}
	
	private ItemListElement getSimpleItemInfo(Integer itemId){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("itemId", itemId);
		try{
			ItemListElement item = getNamedParameterJdbcTemplate().queryForObject("SELECT * FROM items WHERE id = :itemId", params, new BeanPropertyRowMapper<ItemListElement>(ItemListElement.class));
		    item.setLocationList(getListOfItemsLocations(itemId));
		    return item;
		}catch(EmptyResultDataAccessException erdae){
			throw new RuntimeException("Couldnt find item for id: " + itemId);
		}
	}
	
	private ItemLocation saveLocationForItem(Integer userId, Integer itemId, Integer type, String longitude, String latitude){
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", userId);
		params.addValue("longitude", trimString(longitude, Fields.longitude.length, StringTypes.ERROR_NOT_EMPTY, Fields.longitude.fieldName));
		params.addValue("latitude", trimString(latitude, Fields.latitude.length, StringTypes.ERROR_NOT_EMPTY, Fields.latitude.fieldName));
		params.addValue("type", type);
		params.addValue("itemId", itemId);
		int i = getNamedParameterJdbcTemplate().update("INSERT INTO locations (longitude, latitude, type, userId, itemId) VALUES (:longitude, :latitude, :type, :userId, :itemId)", params, keyHolder);
		if(i == 1){
			params.addValue("locationId", keyHolder.getKey().intValue());
			return getNamedParameterJdbcTemplate().query("SELECT * FROM locations WHERE id = :locationId", params, new BeanPropertyRowMapper<ItemLocation>(ItemLocation.class)).get(0);
		}else
			throw new RuntimeException("error inserting location");
	}

	@Override
	public ItemListElement putItem(NewItem item, String token) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", getUserIdForToken(token));
		params.addValue("code", trimString(item.getCode(), Fields.code.length, StringTypes.ERROR_NOT_EMPTY, Fields.code.fieldName));
		params.addValue("longitude", trimString(item.getLongitude(), Fields.longitude.length, StringTypes.ERROR_NOT_EMPTY, Fields.longitude.fieldName));
		params.addValue("latitude", trimString(item.getLatitude(), Fields.latitude.length, StringTypes.ERROR_NOT_EMPTY, Fields.latitude.fieldName));
		params.addValue("locationType", item.getType());
		params.addValue("name", trimString(item.getName(), Fields.name.length, StringTypes.ERROR_NOT_EMPTY, Fields.name.fieldName));
		params.addValue("userComment", trimString(item.getUserComment(), Fields.userComment.length, StringTypes.ERROR_NOT_EMPTY, Fields.userComment.fieldName));
		KeyHolder keyHolder = new GeneratedKeyHolder();
		Integer i = getNamedParameterJdbcTemplate().update("INSERT INTO items (name, userComment) VALUES (:name, :userComment)", params, keyHolder);
		if(i == 1){
			int itemId = keyHolder.getKey().intValue();
			params.addValue("itemId", itemId);
			i = getNamedParameterJdbcTemplate().update("INSERT INTO codes (code, itemId) VALUES (:code, :itemId)", params, keyHolder);
			if(i == 1){
				params.addValue("codeId", keyHolder.getKey().intValue());
				i = getNamedParameterJdbcTemplate().update("INSERT INTO locations (longitude, latitude, type, userId, itemId) VALUES (:longitude, :latitude, :locationType, :userId, :itemId)", params, keyHolder);
				if(i == 1){
					params.addValue("locationId", keyHolder.getKey().intValue());
					params.addValue("type", History.create.value);
					params.addValue("details", "");
					i = getNamedParameterJdbcTemplate().update("INSERT INTO itemHistory (type, details, itemId, userId) VALUES (:type, :details, :itemId, :userId)", params, keyHolder);
					if(i == 1){
						params.addValue("historyId", keyHolder.getKey().intValue());
						List<ItemListElement> items = getNamedParameterJdbcTemplate().query("SELECT items.id, items.name, items.userComment, items.status, codes.code FROM items, (SELECT TOP 1 code FROM codes WHERE itemId = :itemId ORDER BY id desc) codes WHERE items.id = :itemId", params, new BeanPropertyRowMapper<ItemListElement>(ItemListElement.class));
						if(items.size() == 1){
							i = getNamedParameterJdbcTemplate().update("INSERT INTO itemOwned (userId, itemId) VALUES (:userId, :itemId)", params, keyHolder);
							if(i == 1){
								items.get(0).setLocationList(getListOfItemsLocations(itemId));
								return items.get(0);
							}else
								throw new RuntimeException("error inserting into itemOwned");
						}else
							throw new RuntimeException("error finding just inserted item");
					}else
						throw new RuntimeException("error inserting new item history");
				}else
					throw new RuntimeException("Error inserting new location");
			}else
				throw new RuntimeException("Error inserting new code");
		}else
			throw new RuntimeException("error inserting new item");
	}

	@Override
	public List<ItemListElement> getAllItemsForUser(String token) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", getUserIdForToken(token));
		params.addValue("activeItemOwnedStatus", Status.activeItemOwned.value);
		params.addValue("tokenActiveStatus", Status.activeToken.value);
		List<ItemListElement> items = getNamedParameterJdbcTemplate().query("SELECT id FROM items WHERE id in (SELECT itemId FROM itemOwned WHERE userId = :userId AND status = :activeItemOwnedStatus)", params, new BeanPropertyRowMapper<ItemListElement>(ItemListElement.class));
		for(int i = 0; i < items.size(); i++){
			params.addValue("itemId", items.get(i).getId());
			items.set(i, getNamedParameterJdbcTemplate().query("SELECT items.id, items.name, items.status, items.userComment, codes.code FROM items, (SELECT TOP 1 code FROM codes WHERE itemId = :itemId ORDER BY id desc) codes WHERE items.id = :itemId", params, new BeanPropertyRowMapper<ItemListElement>(ItemListElement.class)).get(0));
//			items.get(i).setHistoryList(getNamedParameterJdbcTemplate().query("SELECT * FROM itemHistory WHERE itemId = :itemId ORDER BY id desc", params, new BeanPropertyRowMapper<ItemHistory>(ItemHistory.class)));
			items.get(i).setLocationList(getNamedParameterJdbcTemplate().query("SELECT * FROM locations WHERE itemId = :itemId ORDER BY id desc", params, new BeanPropertyRowMapper<ItemLocation>(ItemLocation.class)));
//			items.get(i).setCodeList(getNamedParameterJdbcTemplate().query("SELECT * FROM codes WHERE itemId = :itemId ORDER BY id desc", params, new BeanPropertyRowMapper<ItemCode>(ItemCode.class)));
		}
		return items;
	}

	@Override
	public List<ItemListElement> removeItemFromUser(String itemId, String token) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", getUserIdForToken(token));
		params.addValue("itemId", itemId);
		params.addValue("inactiveItemOwnedStatus", Status.inactiveToken.value);
		int i = getNamedParameterJdbcTemplate().update("UPDATE itemOwned SET status = :inactiveItemOwnedStatus WHERE itemId = :itemId AND userId = :userId", params);
		if(i == 1){
			return getAllItemsForUser(token);
		}else if(i == 0){
			throw new RuntimeException("item does not exist in user's item list, make sure you are using up to date data, or itemId doesnt exist");
		}else
			throw new RuntimeException("more than one item found for an itemId and userId");
	}

	@Override
	public Token loginUser(String email, String password) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", trimString(email, Fields.email.length, StringTypes.ERROR_NOT_EMPTY, Fields.email.fieldName));
		params.addValue("password", trimString(password, Fields.password.length, StringTypes.ERROR_NOT_EMPTY, Fields.password.fieldName));
		params.addValue("activeUserStatus", Status.activeUser.value);
		List<User> users = getNamedParameterJdbcTemplate().query("SELECT id FROM users WHERE email = :email AND password = :password AND status = :activeUserStatus", params, new BeanPropertyRowMapper<User>(User.class));
		if(users.size() == 1){
			String newToken = generateToken();
			params.addValue("token", newToken);
			params.addValue("userId", users.get(0).getId());
			params.addValue("type", Type.tokenDevice.value);
			KeyHolder keyHolder = new GeneratedKeyHolder();
			int i = getNamedParameterJdbcTemplate().update("INSERT INTO tokens (token, userId, type) VALUES(:token, :userId, :type)", params, keyHolder);
			if(i == 1){
				params.addValue("tokenId", keyHolder.getKey().intValue());
				List<Token> tokens = getNamedParameterJdbcTemplate().query("SELECT * FROM tokens WHERE id = :tokenId", params, new BeanPropertyRowMapper<Token>(Token.class));
				if(tokens.size() == 1)
					return tokens.get(0);
				else if(tokens.size() > 1)
					throw new RuntimeException("more than one token found for the just inserted supposedly unique token");
				else
					throw new RuntimeException("couldnt find the token that was just inserted");
			}else
				throw new RuntimeException("insert into tokens failed");
		}else if(users.size() == 0){
			throw new RuntimeException("email or password is invalid");
		}else
			throw new RuntimeException("multiple users found for the same username and password");
	}

	@Override
	public User getUserInfo(String token) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", getUserIdForToken(token));
		params.addValue("token", token);
		params.addValue("activeStatus", Status.activeToken.value);
		try{
			return getNamedParameterJdbcTemplate().queryForObject("SELECT TOP 1 firstName, lastName, email, created, status, type FROM users WHERE id = :userId", params, new BeanPropertyRowMapper<User>(User.class));
		}catch(EmptyResultDataAccessException erdae){
			throw new RuntimeException("found more than one user for that token: " + token + ", userId: " + getUserIdForToken(token) + ", error: " + erdae.getMessage());
		}
	}

	@Override
	public DoesUserExist doesUserExist(String email) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", trimString(email, Fields.email.length, StringTypes.ERROR_NOT_EMPTY, Fields.email.fieldName));
		int i = getNamedParameterJdbcTemplate().query("SELECT id FROM users WHERE email = :email", params, new BeanPropertyRowMapper<DoesUserExist>(DoesUserExist.class)).size();
		DoesUserExist doe = new DoesUserExist();
		doe.setEmail(email);
		if(i == 1)
			doe.setExists(1);
		else if(i == 0)
			doe.setExists(0);
		else
			throw new RuntimeException("found: " + i + " users with the email: " + email);
		return doe;
	}

	@Override
	public Integer logoutUser(String token) {
		getUserIdForToken(token);
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("token", token);
		params.addValue("inactiveStatus", Status.inactiveToken.value);
		int i = getNamedParameterJdbcTemplate().update("UPDATE tokens SET status = :inactiveStatus WHERE token = :token", params);
		return i;
	}

	@Override
	public List<SecurityQuestion> getSecurityQuestions(String email) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", trimString(email, Fields.email.length, StringTypes.ERROR_NOT_EMPTY, Fields.email.fieldName));
		params.addValue("status", Status.activeSecurityQuestion.value);
		return getNamedParameterJdbcTemplate().query("SELECT id, question, userId FROM securityQuestions WHERE userId = (SELECT id FROM users WHERE email = :email) AND  status = :status", params, new BeanPropertyRowMapper<SecurityQuestion>(SecurityQuestion.class));
	}

	@Override
	public User putUser(User newUser) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lastName", trimString(newUser.getLastName(), Fields.lastName.length, StringTypes.ERROR_NOT_EMPTY, Fields.lastName.fieldName));
		params.addValue("firstName", trimString(newUser.getFirstName(), Fields.firstName.length, StringTypes.ERROR_NOT_EMPTY, Fields.firstName.fieldName));
		params.addValue("email", trimString(newUser.getEmail(), Fields.email.length, StringTypes.ERROR_NOT_EMPTY, Fields.email.fieldName));
		params.addValue("password", trimString(newUser.getPassword(), Fields.password.length, StringTypes.ERROR_NOT_EMPTY, Fields.password.fieldName));
		KeyHolder keyHolder = new GeneratedKeyHolder();
		if(doesUserExist(newUser.getEmail()).getExists() == 0){
			Integer j = getNamedParameterJdbcTemplate().update("INSERT INTO users (firstName, lastName, email, password) VALUES (:firstName, :lastName, :email, :password)", params, keyHolder);
			if(j == 1){
				params.addValue("userId", keyHolder.getKey().intValue());
				for(int i = 0; i < newUser.getSecurityQuestions().size(); i++){
					params.addValue("question", newUser.getSecurityQuestions().get(i).getQuestion());
					params.addValue("answer", newUser.getSecurityQuestions().get(i).getAnswer());
					j = getNamedParameterJdbcTemplate().update("INSERT INTO securityQuestions (question, answer, userId) VALUES (:question, :answer, :userId); SELECT scope_identity()", params, keyHolder);
					if(j != 1){
						throw new RuntimeException("error inserting new security question for user: " + newUser.getEmail());
					}
				}
				return getNamedParameterJdbcTemplate().query("SELECT * FROM users WHERE id = :userId", params, new BeanPropertyRowMapper<User>(User.class)).get(0);
			}else
				throw new RuntimeException("error inserting new user");
		}else
			throw new RuntimeException("The email: " + newUser.getEmail() + " already exists in the database");
	}

	@Override
	public List<ItemLocation> getLocationsForItem(String token, Integer id) {
		if(doesUserHaveItem(token, id))
			return getListOfItemsLocations(id);
		else
			throw new RuntimeException("user doesnt have the rights to see that item");
	}

	@Override
	public ItemListElement putNewLocationForExistingItem(ItemLocation location) {
		saveLocationForItem(location.getUserId(), location.getItemId(), location.getType(), location.getLongitude(), location.getLatitude());
		return getSimpleItemInfo(location.getItemId());
	}
}









