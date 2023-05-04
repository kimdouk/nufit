package com.fit.nufit.food.presentation;

import com.fit.nufit.food.application.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api")
@RequiredArgsConstructor
@RestController
public class FoodController {

    private final FoodService foodService;

}
