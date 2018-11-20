package com.threedsoft.config.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.junit.Test;

public class GitRepositoryTest {

	public void testGitRepo() throws InvalidRemoteException, TransportException, GitAPIException {
		String REMOTE_URL = "https://github.com/knpillutla/appconfig";
		Collection<Ref> refs = Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(REMOTE_URL).call();

		for (Ref ref : refs) {
			System.out.println("Ref: " + ref);
		}

		final Map<String, Ref> map = Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(REMOTE_URL)
				.callAsMap();

		System.out.println("As map");
		for (Map.Entry<String, Ref> entry : map.entrySet()) {
			System.out.println("Key: " + entry.getKey() + ", Ref: " + entry.getValue());
			Ref ref = entry.getValue();
			System.out.println("ref.getname: " + ref.getName());
		}

		refs = Git.lsRemoteRepository().setRemote(REMOTE_URL).call();

		System.out.println("All refs");
		for (Ref ref : refs) {
			System.out.println("Ref: " + ref);
		}

	}

	//@Test
	public void printDirectory() throws IOException, InvalidRemoteException, TransportException, GitAPIException {
		String busName = "XYZ";
		Integer locnNbr = 3456;
		String REMOTE_URL = "https://github.com/knpillutla/appconfig";
		Collection<Ref> refs = Git.lsRemoteRepository().setHeads(true).setTags(true).setRemote(REMOTE_URL).call();
		File gitFileDir = new File("tmp5");
		Git gitInstance = null;
		try {
			gitInstance = Git.open(gitFileDir);
		} catch (RepositoryNotFoundException ex) {
			ex.printStackTrace();
		}
		if (gitInstance == null) {
			gitInstance = Git.cloneRepository().setURI(REMOTE_URL).setNoCheckout(true).setBare(true).setGitDir(gitFileDir).call();
		}
		Repository repo = gitInstance.getRepository();
		RevCommit revCommit = null;
		RevTree tree = null;
		RevWalk walk = new RevWalk(repo);
		for (Ref ref : refs) {
			revCommit = walk.parseCommit(ref.getObjectId());
//	        tree = revCommit.getTree();
			break;
		}

//		Ref head = repo.getRef("HEAD");
//		revCommit = walk.parseCommit(head.getObjectId());
		tree = revCommit.getTree();

		TreeWalk treeWalk = new TreeWalk(repo);
		treeWalk.addTree(tree);
		treeWalk.setRecursive(false);
		while (treeWalk.next()) {
			if (treeWalk.isSubtree()) {
				System.out.println("dir: " + treeWalk.getPathString());
				treeWalk.enterSubtree();
			} else {
				System.out.println("file: " + treeWalk.getNameString() + ",pathString:" + treeWalk.getPathString());
				ObjectLoader loader = repo.open(treeWalk.getObjectId(0));
				String fullFilePathFromRepo = treeWalk.getPathString();
				File file = new File(gitFileDir, fullFilePathFromRepo);
				Properties props = new Properties();
				props.load(loader.openStream());
				System.out.println("Properties loaded:" + props);
				
				if(treeWalk.getNameString().equalsIgnoreCase("picking.properties")){
					String newBusFileName = fullFilePathFromRepo.substring(0, fullFilePathFromRepo.lastIndexOf("/")+1) + busName + "-"
							+ fullFilePathFromRepo.substring(fullFilePathFromRepo.lastIndexOf("/")+1);
/*					File busFile = new File(gitFileDir,newBusFileName);
					busFile.createNewFile();
					OutputStream outStream = new FileOutputStream(busFile);
					loader.copyTo(outStream);

					// run the add-call
					gitInstance.add().addFilepattern(busFile.getName()).call();
*/
	                File myFile = new File(repo.getDirectory().getParent(), file.getName());
	                myFile.createNewFile();
	                // run the add-call
	                gitInstance.add()
	                        .addFilepattern("testfile")
	                        .call();
	                
					System.out.println("Added file " + myFile + " to repository at " + repo.getDirectory());
				}
			}
		}

		/*
		 * try (TreeWalk dirWalk = new TreeWalk(repo)) { dirWalk.addTree(tree);
		 * dirWalk.setRecursive(false); //treeWalk.setFilter(PathFilter.create("src"));
		 * while (dirWalk.next()) { System.out.println("file:" +
		 * dirWalk.getPathString()); // FileMode now indicates that this is a directory,
		 * i.e. // FileMode.TREE.equals(fileMode) holds true FileMode fileMode =
		 * dirWalk.getFileMode(0); ObjectLoader loader =
		 * repo.open(dirWalk.getObjectId(0)); System.out.println("README.md: " +
		 * getFileMode(fileMode) + ", type: " + fileMode.getObjectType() + ", mode: " +
		 * fileMode + " size: " + loader.getSize()); System.out.println(
		 * getFileMode(fileMode) + ", type: " + fileMode.getObjectType() + ", mode: " +
		 * fileMode); } }
		 */ }

	private static RevTree getTree(Repository repository) throws IOException {
		ObjectId lastCommitId = repository.resolve(Constants.HEAD);

		// a RevWalk allows to walk over commits based on some filtering
		try (RevWalk revWalk = new RevWalk(repository)) {
			RevCommit commit = revWalk.parseCommit(lastCommitId);

			System.out.println("Time of commit (seconds since epoch): " + commit.getCommitTime());

			// and using commit's tree find the path
			RevTree tree = commit.getTree();
			System.out.println("Having tree: " + tree);
			return tree;
		}
	}

	private static String getFileMode(FileMode fileMode) {
		if (fileMode.equals(FileMode.EXECUTABLE_FILE)) {
			return "Executable File";
		} else if (fileMode.equals(FileMode.REGULAR_FILE)) {
			return "Normal File";
		} else if (fileMode.equals(FileMode.TREE)) {
			return "Directory";
		} else if (fileMode.equals(FileMode.SYMLINK)) {
			return "Symlink";
		} else {
			// there are a few others, see FileMode javadoc for details
			throw new IllegalArgumentException("Unknown type of file encountered: " + fileMode);
		}
	}

}
