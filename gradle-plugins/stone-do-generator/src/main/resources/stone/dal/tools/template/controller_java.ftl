package ${packageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import stone.dal.pojo.repo.${repoClass};
import stone.dal.pojo.jpa.${doName};

@Controller
@RequestMapping("/${doName}")
@Api(value="${className}",description="${className}")
public class ${className} {
  @Autowired
  private ${repoClass} repository;

   @Transactional
   @ApiOperation(httpMethod="GET",value="Query ${doName}",notes="Find ${doName} by uuid")
   @RequestMapping(value="/get${doName}ById/{userId}",method=RequestMethod.GET)
   public @ResponseBody ${doName} get(Long uuid){
        ${doName} object = new ${doName}();
        object.setUuid(uuid);
        return repository.get(object);
   }

   @Transactional
   @ApiOperation(httpMethod="POST",value="Create ${doName}",notes="Create ${doName}")
   @RequestMapping(value="/",method=RequestMethod.POST)
   public Long ${doName} create(@RequestBody ${doName} entity){
        return repository.create(entity);
   }




}
