package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.telegram.Joke;
import org.primefaces.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Random;

@RestController
public class JokeRestController {

    @Value("${telegram.api.url}")
    private String telegramApiUrl;
    @Value("${telegram.api.url.send}")
    private String telegramApiUrlSend;

    private ArrayList<Joke> jokes = new ArrayList<Joke>();
    private RestTemplate restTemplate;

    public JokeRestController()
    {
        jokes.add(new Joke(1,"Titre 1","Pourquoi les ingénieurs en informatique ont-ils du mal à comprendre les blagues ? \n" +
                "Parce qu'ils prennent tout au premier degré..",0));
        jokes.add(new Joke(2,"Titre 2","Pourquoi les développeurs web ne sortent-ils jamais ? \n" +
                "Parce qu'ils perdent leur cache.",0));
        jokes.add(new Joke(3,"Titre 3","Qu'est-ce qu'un algorithme ? \n" +
                "Une recette de cuisine pour ordinateurs.",0));
        jokes.add(new Joke(4,"Titre 4","Pourquoi un ordinateur voudrait-il se gratter?\n" +
                "Parce qu’il a des puces!",0));
        jokes.add(new Joke(5,"Titre 5","Pourquoi les développeurs sont-ils de bons jardiniers ? \n" +
                "Parce qu'ils savent comment planter des arbres binaires.",0));
        jokes.add(new Joke(6,"Titre 6","Pourquoi les ordinateurs ont-ils froid ?\n" +
                " Parce qu'ils laissent Windows ouverts.",0));

        restTemplate = new RestTemplate();
    }

    @GetMapping("/getRandomJoke")
    public Joke getRandomJoke()
    {
        int random = new Random().nextInt(jokes.size());
        return jokes.get(random);
    }

    @GetMapping("/sendJoke")
    public ResponseEntity<Joke> sendJoke()
    {
        Joke joke = getRandomJoke();
        System.out.println("Joke : "+joke);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject messageJsonObject = new JSONObject();
        messageJsonObject.put("chat_id", "1850817748");
        messageJsonObject.put("text", joke.getTexte());

        HttpEntity<String> request = new HttpEntity<String>(messageJsonObject.toString(), headers);

        try {
            String messageResultAsJsonStr = restTemplate.postForObject("https://api.telegram.org/bot6189285093:AAF1wyfYFaare1o28Npv2qgfnqYlMMKcOcM/sendMessage", request, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok().body(joke);
    }

}
