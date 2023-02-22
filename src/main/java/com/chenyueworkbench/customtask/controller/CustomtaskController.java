package com.chenyueworkbench.customtask.controller;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chenyueworkbench.customtask.model.Customtask;
import com.chenyueworkbench.customtask.service.CustomtaskService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value="v1/customtask")
public class CustomtaskController {
		
    @Autowired
    private CustomtaskService service;

    
    // get 
    @RolesAllowed({ "ADMIN", "USER", "SUPER", "SUPERADMIN"})
    @RequestMapping(value="/list/my", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String,Object>> getMyCustomtaskList() {
        return ResponseEntity.ok(createReturnMap(service.getMyList()));
    }

    @RolesAllowed({ "ADMIN", "USER","SUPER", "SUPERADMIN" })
    @RequestMapping(value="/ids/{customtaskIds}",method = RequestMethod.GET)
    public ResponseEntity<HashMap<String,Object>> getCustomtasks( @PathVariable("customtaskIds") List<String> customtaskIds) {
        return ResponseEntity.ok(createReturnMap(service.findByIds(customtaskIds)));
    }

    @RolesAllowed({ "ADMIN", "USER","SUPER","WORKER","WORKERADMIN","SUPERADMIN" })
    @RequestMapping(value="/{customtaskId}",method = RequestMethod.GET)
    public ResponseEntity<Customtask> getCustomtask( @PathVariable("customtaskId") String customtaskId) {
        return ResponseEntity.ok(service.findById(customtaskId));
    }

    @RolesAllowed({ "USER","ADMIN", "SUPER", "SUPERADMIN"})
    @RequestMapping(value="/find/{key}", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String,Object>> findByNameContains( @PathVariable("key") String key) {
        return ResponseEntity.ok(createReturnMap(service.findByNameContains(key)));
    }


    // update
    @RolesAllowed({ "USER","ADMIN", "SUPER", "SUPERADMIN" })
    @RequestMapping(value="/{customtaskId}",method = RequestMethod.PUT)
    public void updateCustomtask( @PathVariable("customtaskId") String id, @RequestBody Customtask customtask) {
        service.update(customtask);
    }

    @RolesAllowed({ "ADMIN", "USER","SUPER" , "SUPERADMIN"})
    @RequestMapping(value="/{customtaskId}/upload/resource_name/{fileName}",method = RequestMethod.PUT)
    public void resourceUpload( @PathVariable("customtaskId") String id, @PathVariable("fileName") String fileName) {
        service.updateResourceUrl(id, fileName);
    }

    @RolesAllowed({ "ADMIN", "USER","SUPER" , "SUPERADMIN"})
    @RequestMapping(value="/{customtaskId}/upload/task_name/{fileName}",method = RequestMethod.PUT)
    public void taskUpload( @PathVariable("customtaskId") String id, @PathVariable("fileName") String fileName) {
        service.updateTaskUrl(id, fileName);
    }

    @RolesAllowed({ "ADMIN", "USER","SUPER" , "SUPERADMIN"})
    @RequestMapping(value="/{customtaskId}/upload/final_name/{fileName}",method = RequestMethod.PUT)
    public void finalUpload( @PathVariable("customtaskId") String id, @PathVariable("fileName") String fileName) {
        service.updateFinalUrl(id, fileName);
    }

    @RolesAllowed({ "SUPER" ,"WORKERADMIN", "SUPERADMIN"})
    @PutMapping(value="/customtaskId/{customtaskId}/worker_name/{workerName}")
    public void UpdateWorkerName( @PathVariable("customtaskId") String id, @PathVariable("workerName") String workerName) {
        service.updateWorker(id, workerName);
    }

    @RolesAllowed({ "ADMIN", "SUPER", "SUPERADMIN"})
    @PutMapping(value="/customtaskId/{customtaskId}/disable")
    public ResponseEntity<HashMap<String, Object>> disableCustomtask( @PathVariable("customtaskId") String id) {
        return ResponseEntity.ok(createReturnMap(service.disable(id)));
    }

    // @RolesAllowed({ "ADMIN", "USER","SUPER" , "SUPERADMIN"})
    // @RequestMapping(value="/{customtaskId}/disable",method = RequestMethod.PUT)
    // public ResponseEntity<HashMap<String, Object>> void fileUpload( @PathVariable("customtaskId") String id) {
    //     return ResponseEntity.ok(createReturnMap(service.disable(id)));
        
    // }


    // create
    @RolesAllowed({ "ADMIN", "USER","SUPER", "SUPERADMIN" })
    @PostMapping
    public ResponseEntity<HashMap<String, Object>>  saveCustomtask(@RequestBody Customtask customtask) {
    	return ResponseEntity.ok(createReturnMap(service.create(customtask)));
    }


    // delete
    @RolesAllowed({"SUPER", "SUPERADMIN"})
    @RequestMapping(value="/{customtaskId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomtask( @PathVariable("customtaskId") String customtaskId) {
    	service.delete( customtaskId );
    }


    //private 
    private HashMap<String, Object> createReturnMap(Object obj){
        HashMap<String, Object> returnMap = new HashMap<>();
        returnMap.put("data", obj);
        return returnMap;
    }

}
