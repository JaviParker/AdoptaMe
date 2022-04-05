package mx.com.adoptame.entities.pet.controllers;

import mx.com.adoptame.entities.character.CharacterService;
import mx.com.adoptame.entities.color.ColorService;
import mx.com.adoptame.entities.pet.entities.Pet;
import mx.com.adoptame.entities.pet.services.PetService;
import mx.com.adoptame.entities.size.SizeService;
import mx.com.adoptame.entities.type.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/pets")
public class PetController {

    @Autowired private PetService petService;

    @Autowired private CharacterService characterService;

    @Autowired private ColorService colorService;

    @Autowired private SizeService sizeService;

    @Autowired private TypeService typeService;

    private Logger logger = LoggerFactory.getLogger(PetController.class);

    @GetMapping("/")
    public String pets(Model model) {
        model.addAttribute("fix", "fix");
        model.addAttribute("list", petService.findLastThreePets());
        return "views/pets/pets";
    }

    @GetMapping("/{id}")
    public String pet(@PathVariable("id") Integer id ,Model model, RedirectAttributes redirectAttributes) {
        try{
            Optional<Pet> pet = petService.findOne(id);
            if(pet.isPresent() && pet.get().getIsActive()){
                model.addAttribute("pet", pet.get());
                return "views/pets/pet";
            }else{
                redirectAttributes.addFlashAttribute("msg_error", "Elemento no encontrado");
                return "redirect:/pets/";
            }
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("msg_error", "Elemento no encontrado");
            logger.error(e.getMessage());
            return "redirect:/pets/";
        }
    }


    @GetMapping(value = {"/filter"})
    public String filter(Model model) {
        model.addAttribute("typeList", typeService.findAll());
        model.addAttribute("sizeList", sizeService.findAll());
        model.addAttribute("characterList", characterService.findAll());
        model.addAttribute("colorList", colorService.findAll());
        model.addAttribute("petsList", petService.findPetsForAdopted());
//         TODO realizar filtarado
        return "views/pets/petsFilter";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("list", petService.findAll());
        return "views/pets/petsList";
    }

    @GetMapping("/admin/form")
    public String save(Model model, Pet pet) {
        model.addAttribute("listCharacters", characterService.findAll());
        model.addAttribute("listColors", colorService.findAll());
        model.addAttribute("listSizes", sizeService.findAll());
        model.addAttribute("listTypes", typeService.findAll());
        return "views/pets/petsForm";
    }

    @GetMapping("/admin/request")
    public String request(Model model) {
        model.addAttribute("list", petService.findAllisActiveFalse());
        return "views/pets/petsRequest";
    }

    @GetMapping("/admin/acept/{id}")
    public String acept(@PathVariable("id") Integer id,Model model, Pet pet, RedirectAttributes redirectAttributes) {
        if (petService.accept(id)) {
            redirectAttributes.addFlashAttribute("msg_success", "Mascota aceptada exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("msg_error", "Mascota no aceptada");
        }
        return "redirect:/pets/admin/request";
    }


    @GetMapping("/admin/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, Pet pet, RedirectAttributes redirectAttributes) {
        pet = petService.findOne(id).orElse(null);
        if (pet == null) {
            redirectAttributes.addFlashAttribute("msg_error", "Elemento no encontrado");
            return "redirect:/pets/admin";
        }
        model.addAttribute("listCharacters", characterService.findAll());
        model.addAttribute("listColors", colorService.findAll());
        model.addAttribute("listSizes", sizeService.findAll());
        model.addAttribute("listTypes", typeService.findAll());
        model.addAttribute("pet", pet);
        return "views/pets/petsForm";
    }

    @PostMapping("/admin/save")
    public String save(Model model, @Valid Pet pet, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        try {
            pet.setIsActive(false);
            pet.setIsAdopted(false);
            if (bindingResult.hasErrors()) {
                model.addAttribute("listCharacters", characterService.findAll());
                model.addAttribute("listColors", colorService.findAll());
                model.addAttribute("listSizes", sizeService.findAll());
                model.addAttribute("listTypes", typeService.findAll());
                return "views/pets/petsForm";
            } else {
                petService.save(pet);
                redirectAttributes.addFlashAttribute("msg_success", "Mascota guardado exitosamente");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/pets/admin";
    }

    @GetMapping("/admin/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, Pet pet, RedirectAttributes redirectAttributes) {
        if (Boolean.TRUE.equals(petService.delete(id))) {
            redirectAttributes.addFlashAttribute("msg_success", "Mascota eliminado exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("msg_error", "Mascota no eliminado");
        }
        return "redirect:/pets/admin";
    }
}
