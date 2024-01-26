package com.example.hellopizza.controller;

import com.example.hellopizza.model.Pizza;
import com.example.hellopizza.repository.PizzaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class PizzaController {
    @Autowired
    private PizzaRepository pizzaRepository;
    @GetMapping("/lista")
    public String index(Model model) {
        List<Pizza> pizzaList = pizzaRepository.findAll();
        model.addAttribute("pizzaList", pizzaList);

        return "list";
    }

    @GetMapping("/search")
    public String index(@RequestParam(name = "keyword", required = false) String searchKeyword,
                        Model model) {
        List<Pizza> pizzaList;
        if (searchKeyword != null) {
            pizzaList = pizzaRepository.findByNomeContaining(searchKeyword);
        } else {
            pizzaList = pizzaRepository.findAll();
        }
        model.addAttribute("pizzaList", pizzaList);
        model.addAttribute("preloadSearch", searchKeyword);
        return "list";
    }

    @GetMapping("/lista/{id}")
    public String index_due(@PathVariable Integer id,Model model) {
        Optional<Pizza> result = pizzaRepository.findById(id);
        if(result.isPresent()) {
            Pizza pizza= result.get();
            model.addAttribute("pizza",pizza);
            return "details";
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id " + id + " not found");
        }


    }

    @GetMapping("/create")
    public String create(Model model) {
        Pizza pizza = new Pizza();
        model.addAttribute("pizza", pizza);
        return "create";
    }

    @PostMapping("/create")
    public String store(@Valid @ModelAttribute("pizza") Pizza formPizza, BindingResult bindingResult) {
        // valido i dati del Book, cioè verifico se la mappa BindingResult ha errori
        if (bindingResult.hasErrors()) {
            // qui gestisco che ho campi non validi
            // ricaricando il template del form
            return "create";
        }

        Optional<Pizza> NomePizza = pizzaRepository.findByNome(formPizza.getNome());
        if (NomePizza.isPresent()) {
            bindingResult.addError(new FieldError("pizza", "nome", formPizza.getNome(), false, null, null,
                    "La pizza già c'è"));
            return "create";
        } else {
            formPizza.setCreatedAt(LocalDate.now());
            Pizza savedPizza = pizzaRepository.save(formPizza);
            return "redirect:/lista/" + savedPizza.getId();
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Optional<Pizza> result = pizzaRepository.findById(id);
        if (result.isPresent()) {
            model.addAttribute("pizza", result.get());
            return "edit";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id " + id + " not found");
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("pizza") Pizza formPizza,
                         BindingResult bindingResult) {
        Optional<Pizza> result = pizzaRepository.findById(id);
        if (result.isPresent()) {
            Pizza pizzaToEdit = result.get();
            if (bindingResult.hasErrors()) {
                return "edit";
            }
            formPizza.setCreatedAt(pizzaToEdit.getCreatedAt());
            Pizza savedPizza = pizzaRepository.save(formPizza);
            return "redirect:/lista/" + id;
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with id " + id + " not found");
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Optional<Pizza> result = pizzaRepository.findById(id);
        if (result.isPresent()) {
            pizzaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("redirectMessage",
                    "Pizza " + result.get().getNome() + " deleted!");
            return "redirect:/lista";
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book with di " + id + " not found");
        }
    }


}
