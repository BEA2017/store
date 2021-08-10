package com.example.store.controllers;

import com.example.store.models.Ingredient;
import com.example.store.repo.IngredientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/ingredients")
@SessionAttributes("currentCategory")
public class IngredientsController {
    @Autowired
    IngredientsRepository repo;

    @ModelAttribute("types")
    public List<String> getTypes(){
        return repo.getTypes();
    }

    public Map<String,List<Ingredient>> getIngredients(String type, int page) throws SQLException {
        Map<String,List<Ingredient>> result=repo.findIngredientsByType(type,page);
        return result;
    }

    @GetMapping("/category")
    public String getByCategory(@RequestParam("type") String type, @RequestParam("page") int page, Model model) throws SQLException {
        Map<String,List<Ingredient>> ingredients=getIngredients(type,page-1);
        int pageCount=repo.getPageCountInCategory(type);;
        model.addAttribute("pageCount",pageCount);
        model.addAttribute("currentCategory",type);
        model.addAttribute("ingredients",ingredients);
        return "ingredients-info";
    }

    @GetMapping
    public String getAll(Model model) throws SQLException {
        Map<String,List<Ingredient>> ingredients=getIngredients(Ingredient.Type.ALL.toString(),0);
        model.addAttribute("currentCategory",Ingredient.Type.ALL.toString());
        model.addAttribute("ingredients",ingredients);
        return "ingredients";
    }

    @PostMapping
    public String addIngregient(Ingredient ingredient, @ModelAttribute("currentCategory") String category) throws SQLException {
        ingredient.setCreated(new Timestamp(System.currentTimeMillis()));
        if(ingredient.getType()==null){
            ingredient.setType(Ingredient.Type.valueOf(category));
        }
        repo.save(ingredient);
        String path=(category.equalsIgnoreCase(Ingredient.Type.ALL.toString())?"/ingredients":"/ingredients/category?type="+category+"&page=0");
        return "redirect:"+path;
    }

    @PostMapping("/delete")
    public String deleteIngredients(@ModelAttribute("currentCategory") String category, @RequestParam("elem")int[] elems) throws SQLException {
        System.out.println(Arrays.toString(elems));
        repo.deleteIngredients(elems);
        return "redirect:/ingredients/category?type="+category+"&page=0";
    }
}
