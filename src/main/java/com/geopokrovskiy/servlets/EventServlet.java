package com.geopokrovskiy.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geopokrovskiy.dto.ResponseResult;
import com.geopokrovskiy.model.File;
import com.geopokrovskiy.model.User;
import com.geopokrovskiy.repository.EventRepository;
import com.geopokrovskiy.repository.FileRepository;
import com.geopokrovskiy.repository.UserRepository;
import com.geopokrovskiy.repository.impl.EventRepositoryImpl;
import com.geopokrovskiy.repository.impl.FileRepositoryImpl;
import com.geopokrovskiy.repository.impl.UserRepositoryImpl;
import com.geopokrovskiy.util.ServletUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileServlet extends HttpServlet {
    private FileRepository fileRepository = new FileRepositoryImpl();
    private EventRepository eventRepository = new EventRepositoryImpl();
    private UserRepository userRepository = new UserRepositoryImpl();
    private ObjectMapper objectMapper = new ObjectMapper();
    private final String uploadFilePath = "C:\\Users\\georg\\OneDrive\\Рабочий стол\\Java\\Evgeniy\\UploadedFiles";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletUtils.setServletParams(req, resp);
        ResponseResult<List<File>> responseResult = new ResponseResult<>();

        DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
        diskFileItemFactory.setRepository(new java.io.File(uploadFilePath));

        ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);

        try {
            List<File> files = new ArrayList<>();
            List<FileItem> fileItems = new ArrayList<>();
            List<FileItem> passedFileItems = servletFileUpload.parseRequest(req);

            if(passedFileItems != null && passedFileItems.size() > 0){

            }

        } catch (Exception e) {
            responseResult.setMessage(e.getMessage());
        }

    }
}
