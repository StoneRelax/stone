package ${packageName};

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
public class ${className} {
  @Autowired
  private ${repoName} repository;

    @RequestMapping(value="/{id}",method=RequestMethod.GET)
    @Operation(method="GET",description="Query ${doName}")
    public @ResponseBody ${doName} findByPk(@Parameter(description = "id") @PathVariable("id") ${pkType} id){
      return repository.findByPk(id);
    }

    @RequestMapping(value="/get-all",method=RequestMethod.GET)
    @Operation(method="GET",description="Query ${doName}")
    public @ResponseBody java.util.Collection<${doName}> findAll(){
        return repository.findAll();
    }

    @Transactional
    @RequestMapping(value="",method=RequestMethod.POST)
    @Operation(method="POST",description="Create ${doName}")
    public @ResponseBody ${pkType} create(@RequestBody ${doName} entity){
      return repository.create(entity);
    }

    @Transactional
    @RequestMapping(value="",method=RequestMethod.PUT)
    @Operation(method="PUT",description="Update ${doName}")
    public void update(@RequestBody ${doName} entity){
      repository.update(entity);
    }

    @Transactional
    @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
    @Operation(method="DELETE",description="Delete ${doName}")
    public void delete(@Parameter(description = "id") @PathVariable("id") ${pkType} id){
      repository.delByPk(id);
    }

}
