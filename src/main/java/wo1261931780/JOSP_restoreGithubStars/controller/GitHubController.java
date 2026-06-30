package wo1261931780.JOSP_restoreGithubStars.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wo1261931780.JOSP_restoreGithubStars.config.ShowResult;
import wo1261931780.JOSP_restoreGithubStars.entity.Repositories;
import wo1261931780.JOSP_restoreGithubStars.entity.Repository;
import wo1261931780.JOSP_restoreGithubStars.service.GitHubService;

import java.util.List;

/**
 * GitHub Stars管理控制器
 * @author junw
 */
@RestController
@RequestMapping("/api/repositories")
public class GitHubController {
	@Autowired
	private GitHubService gitHubService;

	/**
	 * 获取用户的starred仓库列表
	 */
	@GetMapping("/stars/{username}")
	public ShowResult<List<Repositories>> getStarredRepositories(@PathVariable String username) {
		List<Repositories> repositories = gitHubService.getStarredRepositories(username);
		return ShowResult.sendSuccess(repositories);
	}

	/**
	 * Star一个仓库
	 */
	@PutMapping("/star")
	public ShowResult<String> starRepository(@RequestBody Repository repository) {
		ResponseEntity<Void> response = gitHubService.starRepository(repository);
		if (response.getStatusCode().is2xxSuccessful()) {
			return ShowResult.sendSuccess("Star成功", "操作成功");
		}
		return ShowResult.sendError("Star失败");
	}

	/**
	 * Unstar一个仓库
	 */
	@DeleteMapping("/unstar")
	public ShowResult<String> unstarRepository(@RequestBody Repository repository) {
		ResponseEntity<Void> response = gitHubService.unstarRepository(repository);
		if (response.getStatusCode().is2xxSuccessful()) {
			return ShowResult.sendSuccess("Unstar成功", "操作成功");
		}
		return ShowResult.sendError("Unstar失败");
	}
}
