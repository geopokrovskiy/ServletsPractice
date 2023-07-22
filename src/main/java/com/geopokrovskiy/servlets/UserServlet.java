package com.geopokrovskiy.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geopokrovskiy.dto.ResponseResult;
import com.geopokrovskiy.model.User;
import com.geopokrovskiy.repository.UserRepository;
import com.geopokrovskiy.repository.impl.UserRepositoryImpl;
import com.geopokrovskiy.util.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class UserServlet extends HttpServlet {

    private ObjectMapper objectMapper = new ObjectMapper();
    private UserRepository userRepository = new UserRepositoryImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletUtils.setServletParams(req, resp);
        ResponseResult<User> responseResult = new ResponseResult<>();
        try {
            User user = this.objectMapper.readValue(req.getReader(), User.class);
            this.userRepository.addNew(user);
            responseResult.setData(user);
            responseResult.setMessage("OK");
            resp.setStatus(200);
        } catch (Exception e) {
            responseResult.setMessage("Incorrect query!");
            resp.setStatus(400);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletUtils.setServletParams(req, resp);
        Long idRequest = ServletUtils.processURI(req, "users");
        if (idRequest == 0L) {
            ResponseResult<List<User>> responseResult = new ResponseResult<>();
            List<User> users = this.userRepository.getAll();
            responseResult.setData(users);
            responseResult.setMessage("OK");
            resp.setStatus(200);
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
        } else if (idRequest > 0L) {
            ResponseResult<User> responseResult = new ResponseResult<>();
            try {
                User user = this.userRepository.getById(idRequest);
                if (user == null) {
                    responseResult.setMessage("User does not exist!");
                    resp.setStatus(404);
                } else {
                    responseResult.setData(user);
                    responseResult.setMessage("OK");
                    resp.setStatus(200);
                }
            } catch (Exception e) {
                responseResult.setMessage(e.getMessage());
                resp.setStatus(500);
            }
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
        } else if (idRequest == -1L) {
            ResponseResult<User> responseResult = new ResponseResult<>();
            responseResult.setMessage("Incorrect id input!");
            resp.setStatus(400);
            this.objectMapper.writeValue(resp.getWriter(), responseResult);
        }

    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletUtils.setServletParams(req, resp);
        ResponseResult<User> responseResult = new ResponseResult<>();
        try {
            User user = this.objectMapper.readValue(req.getReader(), User.class);
            user = this.userRepository.update(user);
            responseResult.setData(user);
            responseResult.setMessage("OK");
            resp.setStatus(200);
        } catch (Exception e) {
            responseResult.setMessage("Incorrect input!");
            resp.setStatus(404);
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletUtils.setServletParams(req, resp);
        ResponseResult<User> responseResult = new ResponseResult<>();
        Long requestId = ServletUtils.processURI(req, "users");
        if (requestId < 1) {
            resp.setStatus(400);
            responseResult.setMessage("Incorrect id format!");
        } else {
            User toDelete = this.userRepository.getById(requestId);
            if (this.userRepository.delete(requestId)) {
                responseResult.setData(toDelete);
                responseResult.setMessage("OK");
                resp.setStatus(200);
            } else {
                responseResult.setMessage("User does not exist!");
                resp.setStatus(404);
            }
        }
        this.objectMapper.writeValue(resp.getWriter(), responseResult);
    }
}
