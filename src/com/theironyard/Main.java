package com.theironyard;

import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        HashMap<String, User> users = new HashMap();
        ArrayList<Message> messages = new ArrayList();

        addTestUsers(users);
        addTestMessages(messages);

        Spark.get(
                "/",
                ((request, response) -> {
                    Session session = request.session();
                    String username =  session.attribute("username");


                    ArrayList<Message> threads = new ArrayList();
                    for (Message message : messages) {
                        if (message.replyId == -1) {
                            threads.add(message);
                        }
                    }

                    HashMap m = new HashMap();
                    m.put("threads", threads);
                    m.put("username", username);
                    return new ModelAndView(m, "threads.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.get(
                "/replies",
                ((request1, response1) -> {
                    HashMap m = new HashMap();
                    String id = request1.queryParams("id");
                    try{
                        int idNum = Integer.valueOf(id);
                        Message message = messages.get(idNum);
                        m.put("message", message);

                        ArrayList<Message> replies = new ArrayList();
                        for (Message msg : messages) {
                            if (msg.replyId == message.id) {
                                replies.add(msg);
                            }
                        }
                        m.put("replies", replies);

                    } catch (Exception e) {

                    }
                    return new ModelAndView(m, "replies.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                ((request, response) -> {
                    String username = request.queryParams("username");
                    String password = request.queryParams("password");

                    if (username.isEmpty() || (password.isEmpty())) {
                        Spark.halt(403);
                    }

                    User user = users.get(username);
                    if (user ==  null) {
                        user = new User();
                        user.password = password;
                        users.put(username, user);
                    }
                    else if (!password.equals(user.password)) {
                        Spark.halt(403);
                    }

                    Session session = request.session();
                    session.attribute("username", username);

                    response.redirect("/");
                    return "";
                })

        );
    }

    static void addTestUsers(HashMap<String, User> users) {
        users.put("Alice", new User());
        users.put("Bob", new User());
        users.put("Charlie", new User());
    }
    static void addTestMessages (ArrayList<Message> messages) {
        messages.add(new Message(0,-1, "Alice", "This is a thread!"));
        messages.add(new Message(1,-1, "Bob", "This is a thread!"));
        messages.add(new Message(2, 0, "Charlie", "Cool thread, Alice!"));
        messages.add(new Message(3, 2, "Alice", "Fuck off Charlie"));

    }
}
