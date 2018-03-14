/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.collabpaint;

import edu.eci.arsw.collabpaint.model.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 *
 * @author Nicolás
 */
@Controller
public class STOMPMessagesHandler {
    
    
    @Autowired
	SimpMessagingTemplate msgt;
        
        ConcurrentMap<String,List> vertices = new ConcurrentHashMap<>();
         
	@MessageMapping("/newpoint.{numdibujo}")    
	public void handlePointEvent(Point pt,@DestinationVariable String numdibujo) throws Exception {
                System.out.println("Nuevo punto recibido en el servidor!:"+pt);
                msgt.convertAndSend("/topic/newpoint."+numdibujo, pt);
                
                
                if(vertices.containsKey(numdibujo)){
                    vertices.get(numdibujo).add(pt);
                    if(vertices.get(numdibujo).size()>=4){
                        msgt.convertAndSend("/topic/newpolygon."+numdibujo, vertices.get(numdibujo));
                        System.out.println("Poligono enviado :"+ vertices.get(numdibujo));
                        vertices.get(numdibujo).clear();
                    }
                }
                else{
                    List<Point> puntos= Collections.synchronizedList( new ArrayList<Point>());
                    puntos.add(pt);
                    vertices.put(numdibujo, puntos);
                }
                
		
	}
}
