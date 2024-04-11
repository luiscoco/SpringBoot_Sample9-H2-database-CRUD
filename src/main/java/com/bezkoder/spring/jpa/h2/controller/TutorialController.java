package com.bezkoder.spring.jpa.h2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bezkoder.spring.jpa.h2.model.Tutorial;
import com.bezkoder.spring.jpa.h2.repository.TutorialRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

//@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
@Tag(name = "Tutorial", description = "The Tutorial API")
public class TutorialController {

  @Autowired
  TutorialRepository tutorialRepository;

  @Operation(summary = "Test the API", description = "Test endpoint to verify the API is working", responses = {
	@ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = "text/plain")) })
	@GetMapping("/test")
	public ResponseEntity<String> test() {
    	return ResponseEntity.ok("Test endpoint response");	
	}

  @Operation(summary = "Test the API", description = "Test endpoint to verify the API is working", responses = {
	@ApiResponse(description = "Success", responseCode = "200", content = @Content(mediaType = "text/plain")) })
	@GetMapping("/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
    try {
			List<Tutorial> tutorials = new ArrayList<Tutorial>();
			if (title == null) {
				tutorialRepository.findAll().forEach(tutorials::add);
			} else {
				tutorialRepository.findByTitleContainingIgnoreCase(title).forEach(tutorials::add);
			}
			
			if (tutorials.isEmpty()) {
				// Add logging here
				System.out.println("No tutorials found");
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			
			// Additional logging here if needed
			System.out.println("Found tutorials: " + tutorials.size());
			return new ResponseEntity<>(tutorials, HttpStatus.OK);
		} catch (Exception e) {
			// Log the exception details here
			e.printStackTrace();
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
  }

  @Operation(summary = "Get a tutorial by ID", description = "Retrieve a single tutorial by its ID", responses = {
	@ApiResponse(description = "Found", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tutorial.class))),
	@ApiResponse(description = "Not Found", responseCode = "404") })
	@GetMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
    Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

    if (tutorialData.isPresent()) {
      return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Operation(summary = "Create a new tutorial", description = "Add a new tutorial to the database", responses = {
	@ApiResponse(description = "Created", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Tutorial.class))),
	@ApiResponse(description = "Internal Server Error", responseCode = "500") })
	@PostMapping("/tutorials")
  public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
    try {
      Tutorial _tutorial = tutorialRepository.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false));
      return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Operation(summary = "Update a tutorial", description = "Update an existing tutorial by ID", responses = {
	@ApiResponse(description = "Successful update", responseCode = "200", content = @Content(schema = @Schema(implementation = Tutorial.class))),
	@ApiResponse(description = "Not found", responseCode = "404")})
	@PutMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
    Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

    if (tutorialData.isPresent()) {
      Tutorial _tutorial = tutorialData.get();
      _tutorial.setTitle(tutorial.getTitle());
      _tutorial.setDescription(tutorial.getDescription());
      _tutorial.setPublished(tutorial.isPublished());
      return new ResponseEntity<>(tutorialRepository.save(_tutorial), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

	@Operation(summary = "Delete a tutorial", description = "Delete a tutorial by ID", responses = {
	@ApiResponse(description = "Successful deletion", responseCode = "204"),
	@ApiResponse(description = "Internal server error", responseCode = "500")})
	@DeleteMapping("/tutorials/{id}")
  public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") long id) {
    try {
      tutorialRepository.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

	@Operation(summary = "Delete all tutorials", description = "Delete all tutorials from the database", responses = {
	@ApiResponse(description = "Successful deletion", responseCode = "204"),
	@ApiResponse(description = "Internal server error", responseCode = "500")})
	@DeleteMapping("/tutorials")
  public ResponseEntity<HttpStatus> deleteAllTutorials() {
    try {
      tutorialRepository.deleteAll();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @Operation(summary = "Get all Published tutorials", description = "Get all Published tutorials from the database", responses = {
	@ApiResponse(description = "Successful deletion", responseCode = "204"),
	@ApiResponse(description = "Internal server error", responseCode = "500")})
  @GetMapping("/tutorials/published")
  public ResponseEntity<List<Tutorial>> findByPublished() {
    try {
      List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

      if (tutorials.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(tutorials, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
