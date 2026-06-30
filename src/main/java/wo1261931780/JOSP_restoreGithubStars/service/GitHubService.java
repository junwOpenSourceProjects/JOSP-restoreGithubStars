package wo1261931780.JOSP_restoreGithubStars.service;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import wo1261931780.JOSP_restoreGithubStars.DTO.GithubDTO;
import wo1261931780.JOSP_restoreGithubStars.client.GithubClient;
import wo1261931780.JOSP_restoreGithubStars.entity.Repositories;
import wo1261931780.JOSP_restoreGithubStars.entity.Repository;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author junw
 */
@Service
@Slf4j
public class GitHubService {
	@Autowired
	private GithubClient githubClient;

	public List<Repositories> getStarredRepositories(String username) {
		List<GithubDTO> githubDTOS = githubClient.getGithubStarList(username);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			// 将对象列表写入 JSON 文件
			objectMapper.writeValue(new File("githubDTOS.json"), githubDTOS);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return convertGithub(githubDTOS);
	}

	public List<Repositories> convertGithub(List<GithubDTO> githubDTOS) {
		List<Repositories> repositories = new ArrayList<>();
		githubDTOS.forEach(githubDTO -> {
			Repositories newOne = new Repositories();
			BeanUtils.copyProperties(githubDTO, newOne);
			if (Objects.isNull(newOne.getId())) {
				newOne.setId(IdUtil.getSnowflakeNextId());
			}
			newOne.setRepositorieName(githubDTO.getName());
			newOne.setCodeLanguage(githubDTO.getLanguage());
			newOne.setHtmlUrl(githubDTO.getHtmlUrl());
			newOne.setForksCount(githubDTO.getForks());
			newOne.setWatchersCount(githubDTO.getWatchers());
			newOne.setPushedAt(parseDate(githubDTO.getPushedAt()));
			newOne.setCreatedAt(parseDate(githubDTO.getCreatedAt()));
			newOne.setUpdatedAt(parseDate(githubDTO.getUpdatedAt()));
			// newOne.setRepositorieName(githubDTO.getFullName());
			if (Objects.nonNull(githubDTO.getLicense())) {
				newOne.setLicenseName(githubDTO.getLicense().getName());
			}
			repositories.add(newOne);
		});
		log.info("{} repositories", repositories.size());
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			// 将对象列表写入 JSON 文件
			objectMapper.writeValue(new File("repositories.json"), repositories);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return repositories;
	}

	// 方法用于 Star 一个仓库
	public ResponseEntity<Void> starRepository(Repository repository) {
		return githubClient.starRepository(repository);
	}

	// 方法用于 Unstar 一个仓库
	public ResponseEntity<Void> unstarRepository(Repository repository) {
		return githubClient.unstarRepository(repository);
	}

	private Date parseDate(String date) {
		if (Objects.isNull(date) || date.isBlank()) {
			return null;
		}
		try {
			return Date.from(Instant.parse(date));
		} catch (Exception e) {
			log.warn("无法解析日期: {}", date);
			return null;
		}
	}
}
