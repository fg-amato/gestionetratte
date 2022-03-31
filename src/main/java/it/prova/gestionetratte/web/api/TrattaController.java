package it.prova.gestionetratte.web.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.gestionetratte.dto.TrattaDTO;
import it.prova.gestionetratte.model.Tratta;
import it.prova.gestionetratte.service.TrattaService;
import it.prova.gestionetratte.web.api.exception.IdNotNullForInsertException;
import it.prova.gestionetratte.web.api.exception.TrattaNotFoundException;

@RestController
@RequestMapping("api/tratta")
public class TrattaController {

	@Autowired
	private TrattaService trattaService;

	@GetMapping
	public List<TrattaDTO> getAll() {
		return TrattaDTO.createTrattaDTOListFromModelList(trattaService.listAllElements(true), true);
	}

	// gli errori di validazione vengono mostrati con 400 Bad Request ma
	// elencandoli grazie al ControllerAdvice
	@PostMapping
	public TrattaDTO createNew(@Valid @RequestBody TrattaDTO trattaInput) {
		// se mi viene inviato un id jpa lo interpreta come update ed a me (producer)
		// non sta bene
		if (trattaInput.getId() != null)
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");
		Tratta trattaInserito = trattaService.inserisciNuovo(trattaInput.buildTrattaModel());
		return TrattaDTO.buildTrattaDTOFromModel(trattaInserito, true);
	}

	@GetMapping("/{id}")
	public TrattaDTO findById(@PathVariable(value = "id", required = true) long id) {
		Tratta tratta = trattaService.caricaSingoloElementoEager(id);

		if (tratta == null)
			throw new TrattaNotFoundException("Tratta not found con id: " + id);

		return TrattaDTO.buildTrattaDTOFromModel(tratta, true);
	}

	@PutMapping("/{id}")
	public TrattaDTO update(@Valid @RequestBody TrattaDTO trattaInput, @PathVariable(required = true) Long id) {
		Tratta tratta = trattaService.caricaSingoloElemento(id);

		if (tratta == null)
			throw new TrattaNotFoundException("Tratta not found con id: " + id);

		trattaInput.setId(id);
		Tratta trattaAggiornato = trattaService.aggiorna(trattaInput.buildTrattaModel());
		return TrattaDTO.buildTrattaDTOFromModel(trattaAggiornato, false);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable(required = true) Long id) {
		trattaService.rimuovi(id);
	}

	@PostMapping("/search")
	public List<TrattaDTO> search(@RequestBody TrattaDTO example, @RequestParam(defaultValue = "0") Integer pageNo,
			@RequestParam(defaultValue = "9") Integer pageSize, @RequestParam(defaultValue = "id") String sortBy) {
		System.out.println(example);
		List<Tratta> tratte = trattaService
				.findByExampleWithPagination(example.buildTrattaModel(), pageNo, pageSize, sortBy).getContent();
		return TrattaDTO.createTrattaDTOListFromModelList(tratte, false);
	}
}
