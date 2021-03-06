package com.smartnote.smartnotebackend.controller;

import com.smartnote.smartnotebackend.dto.VocabularyBuilderDto;
import com.smartnote.smartnotebackend.entity.VocabularyBuilderEntity;
import com.smartnote.smartnotebackend.mapper.VocabularyMapper;
import com.smartnote.smartnotebackend.service.VocabularyBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * The type Vocabulary builder controller.
 */
@RestController
@RequestMapping("/api/vocabulary")
@CrossOrigin(origins = {"http://localhost:3000/", "https://smart-note-react.netlify.app/"})
public class VocabularyBuilderController {

    /**
     * The Task service.
     */
    @Autowired
    private VocabularyBuilderService vocabularyBuilderService;

    /**
     * The Mapper.
     */
    @Autowired
    private VocabularyMapper mapper;

    /**
     * All access string.
     *
     * @return the string
     */
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    /**
     * User access string.
     *
     * @return the string
     */
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    /**
     * Admin access string.
     *
     * @return the string
     */
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    /**
     * Search words by id response entity.
     *
     * @param userId the user id
     * @return the response entity
     */
    @GetMapping(value="/{userId}", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    ResponseEntity<List<VocabularyBuilderDto>> searchWordsById(@PathVariable Long userId)
    {
        List<VocabularyBuilderEntity>  entityList = vocabularyBuilderService.searchWordsById(userId);
        List<VocabularyBuilderDto> dtoList = entityList.stream().map(mapper::toTaskDto).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK).body(dtoList);
    }

    /**
     * Add word response entity.
     *
     * @param request              the request
     * @param vocabularyBuilderDto the vocabulary builder dto
     * @return the response entity
     */
    @PostMapping(consumes= {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    ResponseEntity<String> addWord(HttpServletRequest request, @RequestBody VocabularyBuilderDto vocabularyBuilderDto)
    {

        String location = "";
        String responseBody = "";
        VocabularyBuilderEntity vocabularyBuilderEntity = mapper.toTaskEntity(vocabularyBuilderDto);
        VocabularyBuilderEntity entityResponse = vocabularyBuilderService.addWord(vocabularyBuilderEntity);

        location = request.getRequestURI() + "/"+entityResponse.getId();
        responseBody = String.format("{\"id\": %d}" ,entityResponse.getId());

        return ResponseEntity.status(HttpStatus.CREATED).header(location).body(responseBody);
    }

    /**
     * Delete word.
     *
     * @param id the id
     */
    @DeleteMapping(value="/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteWord(@PathVariable int id)
    {
        vocabularyBuilderService.deleteWord(id);
    }

}
