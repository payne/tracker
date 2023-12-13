package org.mattpayne.rsvp.tracker.invite;

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
@RequestMapping("/invites")
public class InviteController {

    private final InviteService inviteService;

    public InviteController(final InviteService inviteService) {
        this.inviteService = inviteService;
    }

    @GetMapping
    public String list(@RequestParam(name = "filter", required = false) final String filter,
            @SortDefault(sort = "id") @PageableDefault(size = 20) final Pageable pageable,
            final Model model) {
        final SimplePage<InviteDTO> invites = inviteService.findAll(filter, pageable);
        model.addAttribute("invites", invites);
        model.addAttribute("filter", filter);
        model.addAttribute("paginationModel", WebUtils.getPaginationModel(invites));
        return "invite/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("invite") final InviteDTO inviteDTO) {
        return "invite/add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute("invite") @Valid final InviteDTO inviteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "invite/add";
        }
        inviteService.create(inviteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("invite.create.success"));
        return "redirect:/invites";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id, final Model model) {
        model.addAttribute("invite", inviteService.get(id));
        return "invite/edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable(name = "id") final Long id,
            @ModelAttribute("invite") @Valid final InviteDTO inviteDTO,
            final BindingResult bindingResult, final RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "invite/edit";
        }
        inviteService.update(id, inviteDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("invite.update.success"));
        return "redirect:/invites";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") final Long id,
            final RedirectAttributes redirectAttributes) {
        inviteService.delete(id);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("invite.delete.success"));
        return "redirect:/invites";
    }

}
