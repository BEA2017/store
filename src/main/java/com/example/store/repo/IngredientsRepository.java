package com.example.store.repo;

import com.example.store.models.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class IngredientsRepository {
    @Autowired
    DataSource ds;

    public Map<String,List<Ingredient>> findIngredientsByType(String type,int page) throws SQLException {
        Map<String,List<Ingredient>> result=new HashMap<>();
        if(type.equalsIgnoreCase(Ingredient.Type.ALL.toString())){
            List<String> types=getTypes();
            for(String t:types){
                List<Ingredient> ingredients=findIngredientsByTypePaginated(t,page,6);
                if(!ingredients.isEmpty()) result.put(t,ingredients);
            }
        }else{
            List<Ingredient> ingredients=findIngredientsByTypePaginated(type,page,20);
            if(!ingredients.isEmpty()) result.put(type,ingredients);
        }
        return result;
    }

    public List<Ingredient> findIngredientsByTypePaginated(String type,int page,int count) throws SQLException {
        int startPos=count*page;
        PreparedStatement ps=ds.getConnection().prepareStatement("select * from ingredients where type=(?) order by created desc limit (?) offset (?)");
        ps.setString(1,type);
        ps.setInt(2,count);
        ps.setInt(3,startPos);
        ResultSet rs=ps.executeQuery();
        List<Ingredient> result=new ArrayList<>();
        while(rs.next()){
            Ingredient i=new Ingredient(
                    rs.getLong("id"),
                    rs.getString("name"),
                    Ingredient.Type.valueOf(rs.getString("type")),
                    rs.getTimestamp("created")
            );
            result.add(i);
        }
        return result;
    }

    public Ingredient save(Ingredient i) throws SQLException {
        PreparedStatement ps=ds.getConnection().prepareStatement("insert into ingredients (name,type, created) values (?,?,?)");
        ps.setString(1,i.getName());
        ps.setString(2,i.getType().toString());
        ps.setTimestamp(3,i.getCreated());
        ps.execute();
        return i;
    }

    public void deleteIngredients(int[] ids) throws SQLException {
        PreparedStatement ps=ds.getConnection().prepareStatement("delete from ingredients where id=(?)");
        for(int id:ids){
            ps.setInt(1,id);
            ps.addBatch();
        }
        ps.executeBatch();
    }

    public List<String> getTypes(){
        return Arrays.stream(Ingredient.Type.values()).map(i->i.toString()).filter(i->!i.equals("ALL")).collect(Collectors.toList());
    }

    public int getPageCountInCategory(String category) throws SQLException {
        PreparedStatement ps=ds.getConnection().prepareStatement("select count(*) from ingredients where type=(?)");
        ps.setString(1,category);
        ResultSet rs=ps.executeQuery();
        rs.next();
        int itemsInCategory=rs.getInt(1);
        double count=(double)itemsInCategory/20;
        return (int) Math.ceil(count);
    }
}
