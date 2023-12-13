package org.mattpayne.rsvp.tracker.rsvp;

import jakarta.validation.Valid;
import org.mattpayne.rsvp.tracker.model.SimplePage;
import org.mattpayne.rsvp.tracker.util.WebUtils;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/rsvps")
public class RsvpController {

    private final RsvpService rsvpService;

    public RsvpController(final RsvpService rsvpService) {
        this.rsvpService = rsvpService;
    }

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("responseValues", Response.values());
    }

    @GetMapping
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<RsvpDTO> rsvps = rsvpService.findAll(filter, pageable);
        model.addAttribute("rsvps", rsvps);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(rsvps));
        return "rsvp/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("rsvp") final RsvpDTO rsvpDTO) {
        return "rsvp/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("rsvp") @Valid final RsvpDTO rsvpDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "rsvp/add";
        }
        rsvpService.create(rsvpDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("rsvp.create.success"));
        return "redirect:/rsvps";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("rsvp", rsvpService.get(id));
        return "rsvp/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("rsvp") @Valid final RsvpDTO rsvpDTO, final BindingResult bindingResult,
            final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "rsvp/edit";
        }
        rsvpService.update(id, rsvpDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("rsvp.update.success"));
        return "redirect:/rsvps";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        rsvpService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("rsvp.delete.success"));
        return "redirect:/rsvps";
    }

}
