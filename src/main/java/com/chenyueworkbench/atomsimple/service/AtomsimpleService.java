package com.chenyueworkbench.atomsimple.service;

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

import com.chenyueworkbench.atomsimple.events.source.SimpleSourceBean;
import com.chenyueworkbench.atomsimple.model.Atomsimple;
import com.chenyueworkbench.atomsimple.repository.AtomsimpleRepository;
import com.chenyueworkbench.atomsimple.utils.ActionEnum;	
import com.chenyueworkbench.atomsimple.utils.UserContext;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import brave.ScopedSpan;
import brave.Tracer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AtomsimpleService {
		
    @Autowired
    private AtomsimpleRepository repository;
    
    @Autowired
    SimpleSourceBean simpleSourceBean;
    
    @Autowired
	Tracer tracer;


	// get
	public List<Atomsimple> findByIds(List<String> atomsimpleIds)
	{
		return repository.findByIdIn(atomsimpleIds)
							.stream()
							.filter(ip -> isAvailable(ip.getAvailable()))
							.sorted(Comparator.comparing(item -> item.getCreateTime(), Comparator.reverseOrder()))
							.collect(Collectors.toList());
	}

    public Atomsimple findById(String atomsimpleId) {
		Optional<Atomsimple> opt = null;
		ScopedSpan newSpan = tracer.startScopedSpan("Get atomsimple id from db");
		try{
			opt = repository.findById(atomsimpleId);
			simpleSourceBean.publishAtomsimpleChange(ActionEnum.GET, atomsimpleId);		
			if (!opt.isPresent() || !isAvailable(opt.get().getAvailable())) {
				String message = String.format("Unable to find an atomsimple with the Atomsimple id %s", atomsimpleId);
				log.error(message);
				throw new IllegalArgumentException(message);	
			}
			log.debug(getPreferredUsername() + " retrieving Atomsimple Info: " + opt.get().toString());
		}finally{
			newSpan.tag("atomsimple", "db");
			newSpan.annotate("Client received");
			newSpan.finish();
		}
    	return opt.get();
	}

	public ArrayList<Atomsimple> getMyList(){
		ArrayList<Atomsimple> atomsimpleList = new ArrayList<>();
		repository.findAll()
					.stream()
					.filter(ip -> isAvailable(ip.getAvailable()))
					.sorted(Comparator.comparing(item -> item.getCreateTime(), Comparator.reverseOrder()))
					.forEach(atomsimpleList::add);
		return atomsimpleList;
	}

	public ArrayList<Atomsimple> findByNameContains(String name){
		return repository.findByNameContains(name);
    }

	// update
    public void update(Atomsimple atomsimple){
    	repository.save(atomsimple);
        simpleSourceBean.publishAtomsimpleChange(ActionEnum.UPDATED, atomsimple.getId());
    }


	public void updateUrl(String atomsimpleId, String fileName){
		Atomsimple atomsimple = repository.findById(atomsimpleId).get();
		String status = "3";

		atomsimple.setFileUrl(fileName)
					  .setStatus(status)
					  .setModifyTime(generateLocalTimeShangHai());
    	repository.save(atomsimple);
        simpleSourceBean.publishAtomsimpleChange(ActionEnum.UPDATED, atomsimpleId);
    }

	public void updateAvailable(String atomsimpleId, String available){
		try{
			Atomsimple atomsimple = findById(atomsimpleId);
			atomsimple.setAvailable(available)
						.setModifyTime(generateLocalTimeShangHai());
			repository.save(atomsimple);
			simpleSourceBean.publishAtomsimpleChange(ActionEnum.UPDATED, atomsimple.getId());
		}catch(Exception e){
			log.error(e.getMessage());
		}
	}

	public String disable(String atomsimpleId){
		updateAvailable(atomsimpleId, "-1");		
		String message = String.format("%s, target id disabled!", atomsimpleId);
		return message;
	}

	public void updateWorker(String atomsimpleId, String workerName){
		Atomsimple atomsimple = repository.findById(atomsimpleId).get();
		atomsimple.setWorkerName(workerName)
				   .setModifyTime(generateLocalTimeShangHai());
    	repository.save(atomsimple);
		log.debug("Atomsimple update worker username is {}", getPreferredUsername());
        simpleSourceBean.publishAtomsimpleChange(ActionEnum.UPDATED, atomsimple.getId());
    }

	// create
	public Atomsimple create(Atomsimple atomsimple){
		atomsimple.setId(UUID.randomUUID().toString())
					  .setStatus("1")
				      .setCreateTime(generateLocalTimeShangHai())
					  .setContactName(getPreferredUsername())
					  .setHasTemplate(false)
					  .setBeTemplate(false);
        repository.save(atomsimple);
		log.debug("Atomsimple create username is {}", getPreferredUsername());
        simpleSourceBean.publishAtomsimpleChange(ActionEnum.CREATED, atomsimple.getId());
        return atomsimple;
    }

	// delete
    public void delete(String atomsimpleId){
    	repository.deleteById(atomsimpleId);
    	simpleSourceBean.publishAtomsimpleChange(ActionEnum.DELETED, atomsimpleId);
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