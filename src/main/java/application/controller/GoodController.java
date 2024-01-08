package application.controller;

import application.dto.good.GoodRequestDto;
import application.dto.good.GoodResponseDto;
import application.service.GoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/goods")
@Tag(name = "Good management", description = "Endpoints for good management")
public class GoodController {
    private final GoodService goodService;

    @PostMapping
    @Operation(summary = "Create a good", description = "Endpoint for adding new good to the db")
    @ResponseStatus(HttpStatus.CREATED)
    public GoodResponseDto createGood(@Valid @RequestBody GoodRequestDto goodRequestDto) {
        return goodService.createGood(goodRequestDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a good", description = "Endpoint for updating a good by id")
    @ResponseStatus(HttpStatus.OK)
    public GoodResponseDto updateGood(@PathVariable Long id,
                                      @RequestBody GoodRequestDto goodRequestDto) {
        return goodService.updateGood(id, goodRequestDto);
    }
}
