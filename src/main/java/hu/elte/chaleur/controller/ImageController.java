package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.Image;
import hu.elte.chaleur.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ImageController {
    private final ImageService imageService;

    @PostMapping("/upload")
    public void uploadImage(@RequestParam MultipartFile file) throws IOException {
        imageService.uploadImage(file);
    }

    @GetMapping(path = { "/get/{imageName}" })
    public Image getImage(@PathVariable("imageName") String imageName) throws IOException {
        return imageService.getImage(imageName);
    }
}
