package com.acme.service;

import com.acme.domain.ACMEPass;
import com.acme.repository.ACMEPassRepository;
import com.acme.security.SecurityUtils;
import com.acme.service.dto.ACMEPassDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Service Implementation for managing ACMEPass.
 */
@Service
@Transactional
public class ACMEPassService {

	private final Logger log = LoggerFactory.getLogger(ACMEPassService.class);

	@Inject
	private ACMEPassRepository acmePassRepository;

	@Inject
	private UserService userService;

	/**
	 * Save a acmePass.
	 *
	 * @param dto the entity to save
	 * @return the persisted entity
	 */
	public ACMEPassDTO save(ACMEPassDTO dto) {
		log.debug("Request to save ACMEPass : {}", dto);

		ACMEPass acmePass;

		if (dto.getId() != null) {
			acmePass = acmePassRepository.findOne(dto.getId());

			if (acmePass == null) {
				return null;
			}
		} else {
			acmePass = new ACMEPass();
			acmePass.setUser(userService.getCurrentUser());
		}

		acmePass.site(dto.getSite())
			.login(dto.getLogin())
			.password(dto.getPassword());

		return new ACMEPassDTO(acmePassRepository.save(acmePass));
	}

	/**
	 * Get all the acmePasses of current user.
	 *
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<ACMEPassDTO> findAllOfCurrentUser(Pageable pageable) {
		log.debug("Request to get all ACMEPasses of current user");
		return acmePassRepository.findByUserIsCurrentUser(pageable).map(ACMEPassDTO::new);
	}

	/**
	 * Get one acmePass by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Transactional(readOnly = true)
	public ACMEPassDTO findOne(Long id) {
		log.debug("Request to get ACMEPass : {}", id);
		return new ACMEPassDTO(acmePassRepository.findOne(id));
	}

	/**
	 * Delete the acmePass by id.
	 *
	 * @param id the id of the entity
	 */
	public void delete(Long id) {
		log.debug("Request to delete ACMEPass : {}", id);

		if (acmePassRepository.findOne(id).getUser().getEmail().equals(SecurityUtils.getCurrentUser())) {
            acmePassRepository.delete(id);
        }
	}
}
