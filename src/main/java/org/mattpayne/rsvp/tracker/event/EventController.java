package org.mattpayne.rsvp.tracker.event;

import jakarta.validation.Valid;
import org.mattpayne.rsvp.tracker.model.SimplePage;
import org.mattpayne.rsvp.tracker.person.Person;
import org.mattpayne.rsvp.tracker.person.PersonRepository;
import org.mattpayne.rsvp.tracker.util.CustomCollectors;
import org.mattpayne.rsvp.tracker.util.WebUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final PersonRepository personRepository;

    public EventController(final EventService eventService,
            final PersonRepository personRepository) {
        this.eventService = eventService;
        this.personRepository = personRepository;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("peopleValues", personRepository.findAll(Sort.by("id"))
                .stream()
                .collect(CustomCollectors.toSortedMap(Person::getId, Person::getFirstName)));
    }

    @GetMapping
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<EventDTO> events = eventService.findAll(filter, pageable);
        model.addAttribute("events", events);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(events));
        return "event/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("event") final EventDTO eventDTO) {
        return "event/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("event") @Valid final EventDTO eventDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasFieldErrors("name") && eventService.nameExists(eventDTO.getName())) {
            bindingResult.rejectValue("name", "Exists.event.name");
        }
        if (bindingResult.hasErrors()) {
            return "event/add";
        }
        eventService.create(eventDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("event.create.success"));
        return "redirect:/events";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("event", eventService.get(id));
        return "event/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("event") @Valid final EventDTO eventDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        final EventDTO currentEventDTO = eventService.get(id);
        if (!bindingResult.hasFieldErrors("name") &&
                !eventDTO.getName().equalsIgnoreCase(currentEventDTO.getName()) &&
                eventService.nameExists(eventDTO.getName())) {
            bindingResult.rejectValue("name", "Exists.event.name");
        }
        if (bindingResult.hasErrors()) {
            return "event/edit";
        }
        eventService.update(id, eventDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("event.update.success"));
        return "redirect:/events";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        eventService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("event.delete.success"));
        return "redirect:/events";
    }

}
