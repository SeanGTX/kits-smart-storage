package ru.megboyzz.smartstore.controllers;

import org.apache.catalina.util.URLEncoder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.megboyzz.smartstore.util.URIEncoder;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@Controller
public class ContentController {

    @GetMapping("/disks")
    public String testDiskPage(Model model){

        Map<String, String> names_elems = new HashMap<>();
        
        File[] paths = File.listRoots();

        String[] urls = new String[paths.length];
        for (File path : paths) {
            String str = URLEncoder.DEFAULT.encode(String.valueOf(path), StandardCharsets.UTF_8);
            names_elems.put(path.getAbsolutePath(), str);
        }

        
        model.addAttribute("names", names_elems);
        return "disks";
    }
    // TODO шаблонизатор заменить кормальным клинетом
    @GetMapping("/content")
    public String getContent(@RequestParam(value = "path", defaultValue = "") String path, Model model){
        File diskPath = new File(path);

        File[] files = diskPath.listFiles();
        String[] links = new String[files.length];

        Map<String, String> result_links = new HashMap<>();

        for (int i = 0; i < files.length; i++) {
            links[i] = URIEncoder.encodeURIComponent(files[i].getAbsolutePath());
            result_links.put(files[i].getName(), links[i]);
        }



        if(!diskPath.exists()){
            model.addAttribute("dirs", "no_dirs");
        } else
            model.addAttribute("dirs", result_links);
        return "content";
    }

    @RequestMapping(path = "/download", method = RequestMethod.GET)
    public ResponseEntity<Resource> download(@RequestParam(value = "path", defaultValue = "") String path) throws UnsupportedEncodingException {

        System.out.println(path);

        File file = new File(path);
        if(!file.exists()){
            return ResponseEntity.notFound().build();
        }
        else if(file.isDirectory()){
            HttpHeaders headers = new HttpHeaders();
            String encoded = URIEncoder.encodeURIComponent(file.getAbsolutePath());
            headers.add("Location", "/content?path="+encoded);
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }

        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(file));
        }catch (FileNotFoundException e){
            String message = e.getMessage();
            InputStreamResource res = new InputStreamResource(
                    new ByteArrayInputStream(message.getBytes("windows-1251"))
            );
            return new ResponseEntity<>(res, HttpStatus.CONFLICT);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
