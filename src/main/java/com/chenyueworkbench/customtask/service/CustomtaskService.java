package com.chenyueworkbench.customtask.service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chenyueworkbench.customtask.events.source.SimpleSourceBean;
import com.chenyueworkbench.customtask.model.Customtask;
import com.chenyueworkbench.customtask.repository.CustomtaskRepository;
import com.chenyueworkbench.customtask.utils.ActionEnum;
import com.chenyueworkbench.customtask.utils.UserContext;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import brave.ScopedSpan;
import brave.Tracer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomtaskService {
		
    @Autowired
    private CustomtaskRepository repository;
    
    @Autowired
    SimpleSourceBean simpleSourceBean;
    
    @Autowired
	Tracer tracer;


	// get
	public List<Customtask> findByIds(List<String> customtaskIds)
	{
		return repository.findByIdIn(customtaskIds)
							.stream()
							.filter(ip -> isAvailable(ip.getAvailable()))
							.sorted(Comparator.comparing(item -> item.getCreateTime(), Comparator.reverseOrder()))
							.collect(Collectors.toList());
	}

    public Customtask findById(String customtaskId) {
		Optional<Customtask> opt = null;
		ScopedSpan newSpan = tracer.startScopedSpan("Get customtask id from db");
		try{
			opt = repository.findById(customtaskId);
			simpleSourceBean.publishCustomtaskChange(ActionEnum.GET, customtaskId);		
			if (!opt.isPresent() || !isAvailable(opt.get().getAvailable())) {
				String message = String.format("Unable to find an customtask with the Customtask id %s", customtaskId);
				log.error(message);
				throw new IllegalArgumentException(message);	
			}
			log.debug(getPreferredUsername() + " retrieving Customtask Info: " + opt.get().toString());
		}finally{
			newSpan.tag("customtask", "db");
			newSpan.annotate("Client received");
			newSpan.finish();
		}
    	return opt.get();
	}

	public ArrayList<Customtask> getMyList(){
		ArrayList<Customtask> customtaskList = new ArrayList<>();
		repository.findAll()
					.stream()
					.filter(ip -> isAvailable(ip.getAvailable()))
					.sorted(Comparator.comparing(item -> item.getCreateTime(), Comparator.reverseOrder()))
					.forEach(customtaskList::add);
		return customtaskList;
	}

	public ArrayList<Customtask> findByNameContains(String name){
		return repository.findByNameContains(name);
    }

	// update
    public void update(Customtask customtask){
    	repository.save(customtask);
        simpleSourceBean.publishCustomtaskChange(ActionEnum.UPDATED, customtask.getId());
    }

	public void updateResourceUrl(String customtaskId, String resourceUrl){
		Customtask customtask = repository.findById(customtaskId).get();
		String status = "4";

		customtask.setResourceUrl(resourceUrl)
					.setStatus(status)
					.setModifyTime(generateLocalTimeShangHai());
    	repository.save(customtask);
        simpleSourceBean.publishCustomtaskChange(ActionEnum.UPDATED, customtaskId);
    }

	public void updateTaskUrl(String customtaskId, String taskUrl){
		Customtask customtask = repository.findById(customtaskId).get();
		String status = "8";
		customtask.setTaskUrl(taskUrl)
					.setStatus(status)
					.setModifyTime(generateLocalTimeShangHai());
    	repository.save(customtask);
        simpleSourceBean.publishCustomtaskChange(ActionEnum.UPDATED, customtaskId);
    }


	public void updateFinalUrl(String customtaskId, String fileName){
		Customtask customtask = repository.findById(customtaskId).get();
		String status = "5";

		customtask.setFinalUrl(fileName)
					.setStatus(status)
					.setModifyTime(generateLocalTimeShangHai());
    	repository.save(customtask);
        simpleSourceBean.publishCustomtaskChange(ActionEnum.UPDATED, customtaskId);
    }


	public void updateAvailable(String customtaskId, String available){
		try{
			Customtask customtask = findById(customtaskId);
			customtask.setAvailable(available)
						.setModifyTime(generateLocalTimeShangHai());
			repository.save(customtask);
			simpleSourceBean.publishCustomtaskChange(ActionEnum.UPDATED, customtask.getId());
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}

	public String disable(String customtaskId){
		updateAvailable(customtaskId, "-1");		
		String message = String.format("%s, target id disabled!", customtaskId);
		return message;
	}

	public void updateWorker(String customtaskId, String workerName){
		Customtask customtask = repository.findById(customtaskId).get();
		customtask.setWorkerName(workerName)
				   .setModifyTime(generateLocalTimeShangHai());
    	repository.save(customtask);
		log.debug("Customtask update worker username is {}", getPreferredUsername());
        simpleSourceBean.publishCustomtaskChange(ActionEnum.UPDATED, customtask.getId());
    }

	// create
	public Customtask create(Customtask customtask){
		String status = "4";
		if (customtask.getResourceUrl() == null || customtask.getResourceUrl().isEmpty()) {
			status = "1";
		}
		customtask.setId(UUID.randomUUID().toString())
					  .setStatus(status)
				      .setCreateTime(generateLocalTimeShangHai())
					  .setContactName(getPreferredUsername());
        repository.save(customtask);
		log.debug("Customtask create username is {}", getPreferredUsername());
        simpleSourceBean.publishCustomtaskChange(ActionEnum.CREATED, customtask.getId());
        return customtask;
    }

	// delete
    public void delete(String customtaskId){
    	repository.deleteById(customtaskId);
    	simpleSourceBean.publishCustomtaskChange(ActionEnum.DELETED, customtaskId);
    }


	// private 
	private String generateLocalTimeShangHai(){
		LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String strTime = dateTimeFormatter.format(localDateTime);
		return strTime;
	}

	private JSONObject decodeJWT(String JWTToken) {
		String[] split_string = JWTToken.split("\\.");
		String base64EncodedBody = split_string[1];
		Base64 base64Url = new Base64(true);
		String body = new String(base64Url.decode(base64EncodedBody));
		JSONObject jsonObj = new JSONObject(body);
		return jsonObj;
	}

	private JSONObject getJWTJson()
	{
		String jwtObj = UserContext.getAuthToken();
		JSONObject jwtDcode = decodeJWT(jwtObj);
		return jwtDcode;
	}

	private String getPreferredUsername()
	{
		JSONObject jsonObject = getJWTJson();
		String preferred_username = jsonObject.getString("preferred_username");
		log.debug("preferred_username is {}", preferred_username);

		return preferred_username;
	}

	private String getRole()
	{
		JSONObject jsonObject = getJWTJson();
		JSONObject resource_access = jsonObject.getJSONObject("resource_access");
		JSONObject chenyueworkbench = resource_access.getJSONObject("chenyueworkbench");
		JSONArray roles = chenyueworkbench.optJSONArray("roles");
		String role = roles.getString(0);
		log.debug("role is {}", role);
		return role;
	}

	private boolean isBooleanTrue(Boolean beBoolean){
		if (Boolean.TRUE.equals(beBoolean)){
			return true;
		}else{
			return false;
		}
				
	}

	private boolean isAvailable(String beAvailable){
		if (beAvailable.equals("-1")){
			return false;
		}else{
			return true;
		}
	}

}