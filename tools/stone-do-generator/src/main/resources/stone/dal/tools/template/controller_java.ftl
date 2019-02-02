package ${packageName};

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ${doPackage}.${doName};
import ${repoPackage}.${repoName};


@RestController
@RequestMapping("${basePath}/repo/${lowerDoName}")
@Api(value = "${basePath}/repo/${lowerDoName}",description="${className}")
public class ${className} {
  @Autowired
  private ${repoName} repository;

    @RequestMapping(value="/{id}",method=RequestMethod.GET)
    @ApiOperation(httpMethod="GET",value="Query ${doName}")
    public @ResponseBody ${doName} findByPk(@ApiParam(value = "id") @PathVariable("id") ${pkType} id){
      return repository.findByPk(id);
    }

    @RequestMapping(value="/get-all/",method=RequestMethod.GET)
    @ApiOperation(httpMethod="GET",value="Query ${doName}")
    public @ResponseBody java.util.Collection<${doName}> findAll(){
        return repository.findAll();
    }

    @Transactional
    @RequestMapping(value="",method=RequestMethod.POST)
    @ApiOperation(httpMethod="POST",value="Create ${doName}")
    public @ResponseBody ${pkType} create(@RequestBody ${doName} entity){
      return repository.create(entity);
    }

    @Transactional
    @RequestMapping(value="",method=RequestMethod.PUT)
    @ApiOperation(httpMethod="PUT",value="Update ${doName}")
    public void update(@RequestBody ${doName} entity){
      repository.update(entity);
    }

    @Transactional
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    @ApiOperation(httpMethod="DELETE",value="Delete ${doName}")
    public void delete(@ApiParam(value = "id") @PathVariable("id") ${pkType} id){
      repository.delByPk(id);
    }

}
