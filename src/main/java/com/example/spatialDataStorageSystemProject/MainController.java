package com.example.spatialDataStorageSystemProject;
import org.json.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.DataOutputStream;
import java.io.IOException;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RestController
public class MainController {

    Connection c = null;
    Statement stmn = null;
    String text = "";
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();

    private static Logger logger = LoggerFactory.getLogger(MainController.class);


    public void sendRequestToMonitoringSystem(String date, String time, String functionName, String status) throws IOException {
        logger.info(date+" "+time+" "+functionName+" "+status);
        //String url = "https://[Monitoring_system_address]/[Monitoring_system_method]";
        String url ="https://google.com";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        String urlParameters = String.format("&date=%s &time=%s &functionName=%s &status=%s",date,time,functionName,status);

        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        logger.info(Integer.toString(responseCode));
    }

    public void sendRequestToMonitoringSystem(String date, String time, String functionName){
        logger.info(date+" "+time+" "+functionName);
    }
    public void sendRequestToMonitoringSystem(String date, String errorText){
        logger.info(date+" "+errorText);
    }

    @PostMapping(path = "/addObject/{layerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String addObject(
            @RequestBody String enteringJson,
            @PathVariable String layerId
    )
    {
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Сохранить данные: точка, линия, полигон");
        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatialDataStorage","postgres", "postgres");
            stmn = c.createStatement();
            String query = "INSERT INTO geometry_objects(data, layer_id) VALUES (ST_GeomFromGeoJSON('"
                    +enteringJson+"'),"+ layerId+");";
            try{
                stmn.executeUpdate(query);
            }
            catch (Exception sqlException){
                logger.info(date.toString()+" - "+sqlException.getMessage());
                sendRequestToMonitoringSystem(dateFormat.format(date),sqlException.getMessage());
                return sqlException.getMessage();
            }
        }
        catch (Exception e)
        {
            logger.info(date.toString()+" - "+e.getMessage());
            sendRequestToMonitoringSystem(dateFormat.format(date),e.getMessage());
            return e.getMessage();
        }
        //return "answer";
        //text = "Ok";
        try {
            sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Сохранить данные: точка, линия, полигон","Объект сохранён");
        } catch (Exception e) {logger.info(e.getMessage());}
        return "Объект сохранён";
    }

    @GetMapping(path = "/layerObjects/{layerId}")
    public String layerObjects(
            @PathVariable String layerId
    ){
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Получить объекты слоя");
        String resultJSON;
        JSONObject resultJson = new JSONObject();
        JSONObject tempJson;
        JSONArray jsArr = new JSONArray();

        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatialDataStorage","postgres", "postgres");
            stmn = c.createStatement();
            ResultSet rs = stmn.executeQuery(String.format("SELECT id, ST_AsGeoJson(data) FROM geometry_objects WHERE layer_id= %s", layerId));
            while ( rs.next() ) {
                String geoJson = rs.getString("st_asgeojson");
                tempJson = new JSONObject(rs.getString("st_asgeojson"));
                tempJson.put("id",rs.getString("id"));
                //text += typeName;
                jsArr.put(tempJson);
                text += geoJson;
                text += ",";
                text += "\n";
            }
        }
        catch (Exception e)
        {
            logger.info(date.toString()+" - "+e.getMessage());
            sendRequestToMonitoringSystem(dateFormat.format(date),e.getMessage());
            return e.getMessage();
        }

        resultJSON = "{\"objects\":"+jsArr.toString()+"}";
        try{
            sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Получить объекты слоя","Передан GeoJSON");
        }
        catch (Exception e){

        }

        return resultJSON;
    }

    @PostMapping(path = "/createLayer/{layerName}")
    public String createLayer(
            @PathVariable String layerName
    )
    {
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Создать слой");
        String layerID;
        String text2 = "";
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatialDataStorage","postgres", "postgres");
            stmn = c.createStatement();
            String query = String.format("INSERT INTO public.layers(name) VALUES ('%s');",layerName);
            stmn.executeUpdate(query);
        }
        catch (Exception e){
            logger.info(date.toString()+" - "+e.getMessage());
            sendRequestToMonitoringSystem(dateFormat.format(date),e.getMessage());
            return e.getMessage();
        }

        try{
            sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Создать слой","Слой создан");
        }
        catch (Exception e){

        }
        return "Слой создан";
    }

    @PostMapping(path = "/deleteLayer/{layerId}")
    public String deleteLayer(
            @PathVariable String layerId
    )
    {
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Удалить слой");
        String text2 = "";
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatialDataStorage","postgres", "postgres");
            stmn = c.createStatement();
            String query = String.format("DELETE FROM public.layers WHERE id = ('%s');",layerId);
            stmn.executeUpdate(query);
            text2 = String.format("Удалили слой %s",layerId);
        }
        catch (Exception e){
            logger.info(date.toString()+" - "+e.getMessage());
            sendRequestToMonitoringSystem(dateFormat.format(date),e.getMessage());
            return e.getMessage();
        }

        try{
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Удалить слой","Слой удалён");
        }
        catch (Exception e){

        }
        return "Слой удалён";
    }

    @GetMapping(path = "/layers")
    public String layers(

    ){
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Получить перечень слоёв");
        String resultJSON;
        //JSONObject resultJson = new JSONObject();
        JSONObject tempJson;
        JSONArray jsArr = new JSONArray();

        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatialDataStorage","postgres", "postgres");
            stmn = c.createStatement();
            ResultSet rs = stmn.executeQuery("SELECT id, name FROM public.layers");
            while ( rs.next() ) {
                tempJson = new JSONObject();
                tempJson.put("id",rs.getString("id"));
                tempJson.put("name",rs.getString("name"));
                jsArr.put(tempJson);
            }
        }
        catch (Exception e)
        {
            logger.info(date.toString()+" - "+e.getMessage());
            sendRequestToMonitoringSystem(dateFormat.format(date),e.getMessage());
            return e.getMessage();
        }

        resultJSON = "{\"layers\":"+jsArr.toString()+"}";

        try{
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Получить перечень слоёв","Перечень слоёв передан");
        }
        catch (Exception e){

        }
        return resultJSON;
    }

    @GetMapping(path = "/layer_styles/{layerId}")
    public String layerStyles(
            @PathVariable String layerId
    ){
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Получить стили слоя");
        String resultJSON;
        JSONObject resultJson = new JSONObject();
        JSONObject tempJson;
        JSONArray jsArr = new JSONArray();

        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatialDataStorage","postgres", "postgres");
            stmn = c.createStatement();
            ResultSet rs = stmn.executeQuery(String.format("SELECT id, color FROM public.styles WHERE id = %s",layerId));
            while ( rs.next() ) {
                resultJson.put("color",rs.getString("color"));
                resultJson.put("id",rs.getString("id"));

            }
        }
        catch (Exception e)
        {
            logger.info(date.toString()+" - "+e.getMessage());
            sendRequestToMonitoringSystem(dateFormat.format(date),e.getMessage());
            return e.getMessage();
        }

        try{
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Получить стили слоя","Стили слоя переданы");
        }
        catch (Exception e){

        }

        return resultJson.toString();
    }

    @PostMapping(path = "/deleteObject/{objectId}")
    public String deleteObject(
            @PathVariable String objectId
    )
    {
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Удалить данные: точка, линия, полигон");
        text = "";

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatialDataStorage","postgres", "postgres");
            stmn = c.createStatement();
            String query = "DELETE FROM geometry_objects WHERE id = "+objectId;
            stmn.executeUpdate(query);
        }catch (Exception e){
            logger.info(date.toString()+" - "+e.getMessage());
            sendRequestToMonitoringSystem(dateFormat.format(date),e.getMessage());
            return e.getMessage();
        }

        try{
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Удалить данные: точка, линия, полигон","Объект удалён");
        }
        catch (Exception e){

        }

        return "Объект удалён";
    }

    @PostMapping(path = "/saveData", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String saveData(
            @RequestBody String enteringJSON
    )
    {
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Сохранить данные из GeoJSON-а");
        text = "";
        String layer_id = "";
        Statement stmn2 = null;
        Statement stmn3 = null;
        Statement stmn4 = null;
        JSONObject obj;

        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatialDataStorage","postgres", "postgres");
            obj = new JSONObject(enteringJSON);
            stmn2 = c.createStatement();
            String query2 = String.format("INSERT INTO layers(name) VALUES ('%s')",obj.get("layer_name").toString());
            stmn2.executeUpdate(query2);

        }
        catch (Exception e)
        {
            logger.info(date.toString()+" - "+e.getMessage());
            sendRequestToMonitoringSystem(dateFormat.format(date),e.getMessage());
            return e.getMessage();
        }

        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatialDataStorage","postgres", "postgres");
            obj = new JSONObject(enteringJSON);
            stmn3 = c.createStatement();


            String query3 = String.format("SELECT id FROM layers WHERE name = '%s'",obj.get("layer_name").toString());
            ResultSet rs = stmn3.executeQuery(query3);

            while ( rs.next() ) {
                layer_id = rs.getString("id");
            }


        }catch (Exception e){
            logger.info(date.toString()+" - "+e.getMessage());
            sendRequestToMonitoringSystem(dateFormat.format(date),e.getMessage());
            return e.getMessage();
        }

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/spatialDataStorage","postgres", "postgres");
            stmn4 = c.createStatement();
            String query = "";
            obj = new JSONObject(enteringJSON);

            JSONArray objects = (JSONArray) obj.get("objects");
            query = "INSERT INTO geometry_objects(data, layer_id) VALUES ";
            for (int i=0; i<objects.length();i++){
                //text+=objects.get(i);
                JSONObject currentObj = new JSONObject(objects.get(i).toString());
                query += "(ST_GeomFromGeoJSON('"+objects.get(i)+"'),"+layer_id+")";
                if(i!=objects.length()-1)
                    query+=",";
            }
            stmn4.executeUpdate(query);
        }
        catch (Exception e){
            logger.info(date.toString()+" - "+e.getMessage());
            sendRequestToMonitoringSystem(dateFormat.format(date),e.getMessage());
            return e.getMessage();
        }

        try{
        sendRequestToMonitoringSystem(dateFormat.format(date).substring(0,10), dateFormat.format(date).substring(11),"Сохранить данные из GeoJSON-а","Объекты сохранены");
        }
        catch (Exception e){

        }
        return "Объекты сохранены";
    }

}
