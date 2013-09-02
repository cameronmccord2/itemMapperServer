package com.ItemMapper.dao;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
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
import com.ItemMapper.model.Item;
import com.ItemMapper.model.SecurityQuestion;
import com.ItemMapper.model.Token;
import com.ItemMapper.model.User;


public class JDBCItemMapperDAO extends NamedParameterJdbcDaoSupport implements ItemMapperDAO {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	private Integer ACTIVE_TOKEN_STATUS = 1;
	private Integer INACTIVE_TOKEN_STATUS = 0;
	private Integer ACTIVE_SECURITY_QUESTION_STATUS = 1;
	private Integer ACTIVE_ITEM_OWNED_STATUS = 1;
	private Integer INACTIVE_ITEM_OWNED_STATUS = 0;
	private Integer ACTIVE_USER_STATUS = 1;
	private Integer TOKEN_TYPE_DEVICE = 1;
	private Integer ITEM_HISTORY_CREATE = 1;
	private Integer USER_TYPE_LOCATION_GETTER = 1;
	private Integer USER_TYPE_LOCATION_SAVER = 2;
	
	private SecureRandom random = new SecureRandom();
	
	private Integer getUserIdForToken(String token){
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("token", token);
		params.addValue("activeStatus", ACTIVE_TOKEN_STATUS);
		List<Token> tokens = getNamedParameterJdbcTemplate().query("SELECT id FROM tokens WHERE status = :activeStatus AND token = :token", params, new BeanPropertyRowMapper<Token>(Token.class));
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
			String newToken = new BigInteger(130, random).toString(32);
			params.addValue("newToken", newToken);
			int i = getNamedParameterJdbcTemplate().query("SELECT id FROM tokens WHERE token = :newToken", params, new BeanPropertyRowMapper<Token>(Token.class)).size();
			if(i == 0)
				return newToken;
			else if(i > 1)
				throw new RuntimeException("token exists multiple times in tokens table already: " + newToken);
		}
	}

	@Override
	public Item putItem(Item item, String token) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", getUserIdForToken(token));
		params.addValue("code", item.getCode());
		params.addValue("longitude", item.getLocation().getLongitude());
		params.addValue("latitude", item.getLocation().getLatitude());
		params.addValue("howSet", item.getLocation().getHowSet());
		params.addValue("name", item.getName());
		params.addValue("userComment", item.getUserComment());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		Integer i = getNamedParameterJdbcTemplate().update("INSERT INTO items (name, userComment) VALUES (:name, :userComment)", params, keyHolder);
		if(i == 1){
			params.addValue("itemId", keyHolder.getKey().intValue());
			i = getNamedParameterJdbcTemplate().update("INSERT INTO codes (code, itemId) VALUES (:code, :itemId)", params, keyHolder);
			if(i == 1){
				params.addValue("codeId", keyHolder.getKey().intValue());
				i = getNamedParameterJdbcTemplate().update("INSERT INTO locations (longitude, latitude, howSet, userId, itemId) VALUES (:longitude, :latitude, :howSet, :userId, :itemId)", params, keyHolder);
				if(i == 1){
					params.addValue("locationId", keyHolder.getKey().intValue());
					params.addValue("what", ITEM_HISTORY_CREATE);
					params.addValue("details", "");
					i = getNamedParameterJdbcTemplate().update("INSERT INTO itemHistory (what, details, itemId, userId) VALUES (:what, :details, :itemId, :userId)", params, keyHolder);
					if(i == 1){
						params.addValue("historyId", keyHolder.getKey().intValue());
						List<Item> items = getNamedParameterJdbcTemplate().query("SELECT items.id, items.name, items.userComment, codes.code, longitude, locations.latitude, locations.whenSet FROM items, (SELECT TOP 1 code FROM codes WHERE itemId = :itemId ORDER BY id desc) codes, (SELECT TOP 1 longitude, latitude, whenSet FROM locations WHERE itemId = :itemId ORDER BY id desc) locations WHERE items.id = :itemId", params, new BeanPropertyRowMapper<Item>(Item.class));
						if(items.size() == 1){
							return items.get(0);
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
	public List<Item> getAllItemsForUser(String token) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", getUserIdForToken(token));
		params.addValue("token", token);
		params.addValue("activeItemOwnedStatus", ACTIVE_ITEM_OWNED_STATUS);
		params.addValue("tokenActiveStatus", ACTIVE_TOKEN_STATUS);
		List<Item> items = getNamedParameterJdbcTemplate().query("SELECT id FROM items WHERE id in (SELECT id FROM itemOwned WHERE userId = :userId AND status = :activeItemOwnedStatus)", params, new BeanPropertyRowMapper<Item>(Item.class));
		List<Item> finalItems = new ArrayList<Item>();
		for(int i = 0; i < items.size(); i++){
			params.addValue("itemId", items.get(i).getId());
			finalItems.add(getNamedParameterJdbcTemplate().query("SELECT items.id, items.name, items.userComment, codes.code, longitude, locations.latitude, locations.whenSet FROM items, (SELECT TOP 1 code FROM codes WHERE itemId = :itemId ORDER BY id desc) codes, (SELECT TOP 1 longitude, latitude, whenSet FROM locations WHERE itemId = :itemId ORDER BY id desc) locations WHERE items.id = :itemId", params, new BeanPropertyRowMapper<Item>(Item.class)).get(0));
//			items.get(i).setHistory(getNamedParameterJdbcTemplate().query("SELECT * FROM itemHistory WHERE itemId = :itemId ORDER BY id desc", params, new BeanPropertyRowMapper<ItemHistory>(ItemHistory.class)));
//			items.get(i).setLocation(getNamedParameterJdbcTemplate().query("SELECT * FROM locations WHERE itemId = :itemId ORDER BY id desc", params, new BeanPropertyRowMapper<ItemLocation>(ItemLocation.class)));
//			items.get(i).setCode(getNamedParameterJdbcTemplate().query("SELECT * FROM codes WHERE itemId = :itemId ORDER BY id desc", params, new BeanPropertyRowMapper<ItemCode>(ItemCode.class)));
		}
		return finalItems;
	}

	@Override
	public List<Item> removeItemFromUser(String itemId, String token) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", getUserIdForToken(token));
		params.addValue("itemId", itemId);
		params.addValue("inactiveItemOwnedStatus", INACTIVE_ITEM_OWNED_STATUS);
		int i = getNamedParameterJdbcTemplate().update("UPDATE itemOwned SET status = :inactiveItemOwnedStatus WHERE itemId = :itemId AND userId = :userId", params);
		if(i == 1){
			return getAllItemsForUser(token);
		}else if(i == 0){
			throw new RuntimeException("item does not exist in user's item list, make sure you are using up to date data");
		}else
			throw new RuntimeException("more than one item found for an itemId and userId");
	}

	@Override
	public Token loginUser(String email, String password) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", email);
		params.addValue("password", password);
		params.addValue("activeUserStatus", ACTIVE_USER_STATUS);
		List<User> users = getNamedParameterJdbcTemplate().query("SELECT userId FROM users WHERE email = :email AND password = :password AND active = :activeUserStatus", params, new BeanPropertyRowMapper<User>(User.class));
		if(users.size() == 1){
			String newToken = generateToken();
			params.addValue("newToken", newToken);
			params.addValue("userId", users.get(0).getId());
			params.addValue("type", TOKEN_TYPE_DEVICE);
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
		getUserIdForToken(token);
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("token", token);
		params.addValue("activeStatus", ACTIVE_TOKEN_STATUS);
		try{
			return getNamedParameterJdbcTemplate().queryForObject("SELECT TOP 1 * FROM users WHERE id = (SELECT userId FROM tokens WHERE token = 'asdf' AND status = 1)", params, new BeanPropertyRowMapper<User>(User.class));
		}catch(EmptyResultDataAccessException erdae){
			throw new RuntimeException("found more than one user for that token");
		}
	}

	@Override
	public DoesUserExist doesUserExist(String email) {
		if(email.length() == 0)
			throw new RuntimeException("email cannot be blank");
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", email);
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
		params.addValue("inactiveStatus", INACTIVE_TOKEN_STATUS);
		int i = getNamedParameterJdbcTemplate().update("UPDATE tokens SET status = :inactiveStatus WHERE token = :token", params);
		return i;
	}

	@Override
	public List<SecurityQuestion> getSecurityQuestions(String email) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("email", email);
		params.addValue("status", ACTIVE_SECURITY_QUESTION_STATUS);
		return getNamedParameterJdbcTemplate().query("SELECT id, question, userId FROM securityQuestions WHERE userId = (SELECT id FROM users WHERE email = :email) AND  status = :status", params, new BeanPropertyRowMapper<SecurityQuestion>(SecurityQuestion.class));
	}
	
	private String trimStringAndReturn(String source, Integer maxLength, String whatField){
		return trimStringAndReturnOrError(source, maxLength, false, whatField);
	}
	
	private String returnStringOrThrowErrorIfTooLong(String source, Integer maxLength, String whatField){
		return trimStringAndReturnOrError(source, maxLength, true, whatField);
	}
	
	private String trimStringAndReturnOrError(String source, Integer maxLength, boolean throwErrorIfTooLong, String whatField){
		if(source.length() > maxLength){
			if(throwErrorIfTooLong)
				throw new RuntimeException("The string for " + whatField + " was too long. Was: " + source.length() + ", should be no more than: " + maxLength);
			else
				return source.substring(0, maxLength);
		}else
			return source;
	}

	@Override
	public User putUser(User newUser) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("lastName", newUser.getLastName());
		params.addValue("firstName", newUser.getFirstName());
		params.addValue("email", newUser.getEmail());
		params.addValue("password", newUser.getPassword());
		KeyHolder keyHolder = new GeneratedKeyHolder();
		Integer j = getNamedParameterJdbcTemplate().update("INSERT INTO users (firstName, lastName, email, password) VALUES (:firstName, :lastName, :email, :password)", params, keyHolder);
		if(j == 1){
			params.addValue("userId", keyHolder.getKey().intValue());
			for(int i = 0; i < newUser.getSecurityQuestions().size(); i++){
				params.addValue("question", newUser.getSecurityQuestions().get(i).getQuestion());
				params.addValue("answer", newUser.getSecurityQuestions().get(i).getAnswer());
				Integer securityQuestionId = getNamedParameterJdbcTemplate().update("INSERT INTO securityQuestions (question, answer, userId) VALUES (:question, :answer, :userId); SELECT scope_identity()", params);
			}
			return getNamedParameterJdbcTemplate().query("SELECT * FROM users WHERE id = :userId", params, new BeanPropertyRowMapper<User>(User.class)).get(0);
		}else
			throw new RuntimeException("error inserting new user");
	}
	
//	@Override
//	public MissionaryLanguage getMissionaryLanguageByLanguageId(String languageid){
//		try{
//			MapSqlParameterSource params = new MapSqlParameterSource();
//			params.addValue("lid", languageid);
//			MissionaryLanguage missionaryLanguage = getNamedParameterJdbcTemplate().queryForObject("SELECT * FROM missionarylanguage WHERE languageid=:lid", params, new BeanPropertyRowMapper<MissionaryLanguage>(MissionaryLanguage.class));
//			return missionaryLanguage;
//		}catch(EmptyResultDataAccessException erdae){
//			return new MissionaryLanguage();
//		}
//	}
//	
//	@Override
//	public MissionaryLanguage getMissionaryLanguageByTallLanguageId(String talllanguageid){
//		try{
//			MapSqlParameterSource params = new MapSqlParameterSource();
//			params.addValue("tlid", talllanguageid);
//			MissionaryLanguage missionaryLanguage = getNamedParameterJdbcTemplate().queryForObject("SELECT * FROM missionarylanguage WHERE talllanguageid=:tlid", params, new BeanPropertyRowMapper<MissionaryLanguage>(MissionaryLanguage.class));
//			return missionaryLanguage;
//		}catch(EmptyResultDataAccessException erdae){
//			return new MissionaryLanguage();
//		}
//	}
//	
//	@Override
//	public MissionaryLanguage getMissionaryLanguageByName(String name){
//		try{
//			MapSqlParameterSource params = new MapSqlParameterSource();
//			params.addValue("name", name);
//			MissionaryLanguage missionaryLanguage = getNamedParameterJdbcTemplate().queryForObject("SELECT * FROM missionarylanguage WHERE name=:name", params, new BeanPropertyRowMapper<MissionaryLanguage>(MissionaryLanguage.class));
//			return missionaryLanguage;
//		}catch(EmptyResultDataAccessException erdae){
//			return new MissionaryLanguage();
//		}
//	}
//	
//	@Override
//	public List<MissionaryLanguage> getAllMissionaryLanguages(){
//		MapSqlParameterSource params = new MapSqlParameterSource();
//		List<MissionaryLanguage> missionaryLanguages = getNamedParameterJdbcTemplate().query("SELECT * FROM missionarylanguage ORDER BY name", params, new BeanPropertyRowMapper<MissionaryLanguage>(MissionaryLanguage.class));
//		return missionaryLanguages;
//	}
//
//	@Override
//	public TallTask getTallTaskByLanguageId(String languageid) {
//		try{
//			MapSqlParameterSource params = new MapSqlParameterSource();
//			params.addValue("lid", languageid);
//			TallTask talltask = getNamedParameterJdbcTemplate().queryForObject("SELECT * FROM talltaskids WHERE languageid=:lid", params, new BeanPropertyRowMapper<TallTask>(TallTask.class));
//			return talltask;
//		}catch(EmptyResultDataAccessException erdae){
//			return new TallTask();
//		}
//	}
//
//	@Override
//	public TallTask getTallTaskByName(String name) {
//		try{
//			MapSqlParameterSource params = new MapSqlParameterSource();
//			params.addValue("name", name);
//			TallTask talltask = getNamedParameterJdbcTemplate().queryForObject("SELECT * FROM talltaskids WHERE name=:name", params, new BeanPropertyRowMapper<TallTask>(TallTask.class));
//			return talltask;
//		}catch(EmptyResultDataAccessException erdae){
//			return new TallTask();
//		}
//	}
//	
//	@Override
//	public TallTask getTallTaskByTallLanguageId(String talllanguageid) {
//		try{
//			MapSqlParameterSource params = new MapSqlParameterSource();
//			params.addValue("tlid", talllanguageid);
//			TallTask talltask = getNamedParameterJdbcTemplate().queryForObject("SELECT * FROM talltaskids WHERE talllanguageid=:tlid", params, new BeanPropertyRowMapper<TallTask>(TallTask.class));
//			return talltask;
//		}catch(EmptyResultDataAccessException erdae){
//			return new TallTask();
//		}
//	}
//
//	@Override
//	public List<SeniorAudioFlagsToClient> getSeniorMissionaryAudioByLanguageAndCategory(String language, String category) {
//		MapSqlParameterSource params = new MapSqlParameterSource();
//		params.addValue("c", category);
//		params.addValue("l", language);
//		if(language.length() > 30) throw new RuntimeException("language is too long: " + language.length() + ", should be 30 or less");
//		if(category.length() > 20) throw new RuntimeException("category is too long: " + category.length() + ", should be 20 or less");
//		List<SeniorAudioFlagsToClient> missionaryLanguages = getNamedParameterJdbcTemplate().query("SELECT filename, whenChanged, status FROM(SELECT filename, status, whenChanged, rank() over (partition by filename order by whenChanged desc)rnk FROM (SELECT * FROM senioraudioflags WHERE useGroup = 1) WHERE category = :c AND language = :l) WHERE rnk = 1", params, new BeanPropertyRowMapper<SeniorAudioFlagsToClient>(SeniorAudioFlagsToClient.class));
//		if(missionaryLanguages.size() == 0)// if nothing with a useGroup = 1 found, then use ones without that flag set to 1
//			missionaryLanguages = getNamedParameterJdbcTemplate().query("SELECT filename, status FROM(SELECT filename, status, whenChanged, rank() over (partition by filename order by whenChanged desc)rnk FROM senioraudioflags WHERE category = :c AND language = :l) WHERE rnk = 1", params, new BeanPropertyRowMapper<SeniorAudioFlagsToClient>(SeniorAudioFlagsToClient.class));
//		return missionaryLanguages;
//	}
//
//	@Override
//	public Integer putNewSeniorAudioFlag(String language, String category, SeniorAudioFlags body) {
//		if(language.length() > 30) throw new RuntimeException("language is too long: " + language.length() + ", should be 30 or less");
//		if(category.length() > 20) throw new RuntimeException("category is too long: " + category.length() + ", should be 20 or less");
//		if(body.getFilename().length() > 150) throw new RuntimeException("filename is too long: " + body.getFilename().length() + ", should be 150 or less");
//		if(body.getStatus().toString().length() != 1) throw new RuntimeException("status is too long: " + body.getStatus().toString().length() + ", should be 1");
//		if(body.getStatus() != 0 && body.getStatus() != 1) throw new RuntimeException("status is invalid: " + body.getStatus() + ", must be a zero or a 1");
//		if(body.getGroupId().length() > 10) throw new RuntimeException("groupId is invalid: " + body.getGroupId() + ", must be 10 or less digits");
//		MapSqlParameterSource params = new MapSqlParameterSource();
//		params.addValue("c", category);
//		params.addValue("l", language);
//		params.addValue("fn", body.getFilename());
//		params.addValue("s", body.getStatus());
//		params.addValue("gid", body.getGroupId());
//		return getNamedParameterJdbcTemplate().update("INSERT INTO seniorAudioFlags (filename, status, language, category, groupId) VALUES (:fn, :s, :l, :c, :gid)", params);
//	}
//
//	@Override
//	public List<SeniorAudioFlagsToClient> getSeniorMissionaryAudioLanguageHistory(String language) {
//		MapSqlParameterSource params = new MapSqlParameterSource();
//		params.addValue("l", language);
//		if(language.length() > 30) throw new RuntimeException("language is too long: " + language.length() + ", should be 30 or less");
//		List<SeniorAudioFlagsToClient> missionaryLanguages = getNamedParameterJdbcTemplate().query("SELECT * FROM senioraudioflags WHERE language = :l ORDER BY whenChanged desc", params, new BeanPropertyRowMapper<SeniorAudioFlagsToClient>(SeniorAudioFlagsToClient.class));
//		return missionaryLanguages;
//	}
//
//	@Override
//	public Integer disableFlagGroup(String groupId) {
//		return changeUsageFlagForGroup(groupId, 0);
//	}
//
//	@Override
//	public Integer enableFlagGroup(String groupId) {
//		return changeUsageFlagForGroup(groupId, 1);
//	}
//	
//	protected Integer changeUsageFlagForGroup(String groupId, Integer flag){
//		MapSqlParameterSource params = new MapSqlParameterSource();
//		params.addValue("gid", groupId);
//		params.addValue("f", flag);
//		if(groupId.length() > 10) throw new RuntimeException("groupId is too long: " + groupId.length() + ", should be 10 or less");
//		if(flag != 1 && flag != 0) throw new RuntimeException("flag is invalid: " + flag + ", should be the number 1 or 0");
//		return getNamedParameterJdbcTemplate().update("UPDATE senioraudioflags SET useGroup = :f WHERE groupId = :gid", params);
//	}
}









