package com.chenyueworkbench.atomsimple.controller;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.chenyueworkbench.atomsimple.model.Atomsimple;
import com.chenyueworkbench.atomsimple.service.AtomsimpleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value="v1/atomsimple")
public class AtomsimpleController {
		
    @Autowired
    private AtomsimpleService service;

    
    // get 
    @RolesAllowed({ "ADMIN", "USER", "SUPER", "SUPERADMIN"})
    @RequestMapping(value="/list/my", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String,Object>> getMyAtomsimpleList() {
        return ResponseEntity.ok(createReturnMap(service.getMyList()));
    }

    @RolesAllowed({ "ADMIN", "USER","SUPER", "SUPERADMIN" })
    @RequestMapping(value="/ids/{atomsimpleIds}",method = RequestMethod.GET)
    public ResponseEntity<HashMap<String,Object>> getAtomsimples( @PathVariable("atomsimpleIds") List<String> atomsimpleIds) {
        return ResponseEntity.ok(createReturnMap(service.findByIds(atomsimpleIds)));
    }

    @RolesAllowed({ "ADMIN", "USER","SUPER","WORKER","WORKERADMIN","SUPERADMIN" })
    @RequestMapping(value="/{atomsimpleId}",method = RequestMethod.GET)
    public ResponseEntity<Atomsimple> getAtomsimple( @PathVariable("atomsimpleId") String atomsimpleId) {
        return ResponseEntity.ok(service.findById(atomsimpleId));
    }

    @RolesAllowed({ "USER","ADMIN", "SUPER", "SUPERADMIN"})
    @RequestMapping(value="/find/{key}", method = RequestMethod.GET)
    public ResponseEntity<HashMap<String,Object>> findByNameContains( @PathVariable("key") String key) {
        return ResponseEntity.ok(createReturnMap(service.findByNameContains(key)));
    }


    // update
    @RolesAllowed({ "USER","ADMIN", "SUPER", "SUPERADMIN" })
    @RequestMapping(value="/{atomsimpleId}",method = RequestMethod.PUT)
    public void updateAtomsimple( @PathVariable("atomsimpleId") String id, @RequestBody Atomsimple atomsimple) {
        service.update(atomsimple);
    }

    @RolesAllowed({ "ADMIN", "USER","SUPER" , "SUPERADMIN"})
    @RequestMapping(value="/{atomsimpleId}/upload/{fileName}",method = RequestMethod.PUT)
    public void fileUpload( @PathVariable("atomsimpleId") String id, @PathVariable("fileName") String fileName) {
        service.updateUrl(id, fileName);
    }

    @RolesAllowed({ "ADMIN", "USER","SUPER" , "SUPERADMIN"})
    @RequestMapping(value="/{atomsimpleId}/disable",method = RequestMethod.PUT)
    public ResponseEntity<HashMap<String, Object>> void fileUpload( @PathVariable("atomsimpleId") String id) {
        return ResponseEntity.ok(createReturnMap(service.disable(id)));
        
    }


    // create
    @RolesAllowed({ "ADMIN", "USER","SUPER", "SUPERADMIN" })
    @PostMapping
    public ResponseEntity<HashMap<String, Object>>  saveAtomsimple(@RequestBody Atomsimple atomsimple) {
    	return ResponseEntity.ok(createReturnMap(service.create(atomsimple)));
    }


    // delete
    @RolesAllowed({"SUPER", "SUPERADMIN"})
    @RequestMapping(value="/{atomsimpleId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAtomsimple( @PathVariable("atomsimpleId") String atomsimpleId) {
    	service.delete( atomsimpleId );
    }


    //private 
    private HashMap<String, Object> createReturnMap(Object obj){
        HashMap<String, Object> returnMap = new HashMap<>();
        returnMap.put("data", obj);
        return returnMap;
    }

}
