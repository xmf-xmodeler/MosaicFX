package tool.clients.fmmlxdiagrams.xmldatabase;

import java.util.List;

/**
 * The DefaultBranchManager is a Singleton class responsible for managing the default branch
 * across the application's runtime. It provides thread-safe methods to set, retrieve, and validate
 * the default branch.
 */
public class DefaultBranchManager {
    private static DefaultBranchManager instance; // Singleton instance

    private String defaultBranch; // Stores the current default branch

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private DefaultBranchManager() {
        this.defaultBranch = null; // Initialize with no default branch
    }

    /**
     * Retrieves the Singleton instance of the DefaultBranchManager.
     * This method is synchronized to ensure thread safety during instance creation.
     *
     * @return the Singleton instance of DefaultBranchManager.
     */
    public static synchronized DefaultBranchManager getInstance() {
        if (instance == null) {
            instance = new DefaultBranchManager();
        }
        return instance;
    }

    /**
     * Sets the default branch in a thread-safe manner.
     *
     * @param branchName The name of the branch to be set as default.
     */
    public synchronized void setDefaultBranch(String branchName) {
        this.defaultBranch = branchName;
    }

    /**
     * Retrieves the current default branch in a thread-safe manner.
     *
     * @return the name of the default branch, or null if no default branch is set.
     */
    public synchronized String getDefaultBranch() {
        return this.defaultBranch;
    }

    /**
     * Validates if the default branch exists in the provided list of available branches.
     * This method is synchronized to ensure consistent validation across threads.
     *
     * @param availableBranches A list of branches to validate against.
     * @return true if the default branch exists in the list; false otherwise.
     */
    public synchronized boolean isDefaultBranchValid(List<String> availableBranches) {
        return availableBranches.contains(this.defaultBranch);
    }

    /**
     * Clears the default branch in a thread-safe manner.
     * This is useful if the default branch is removed or invalidated.
     */
    public synchronized void clearDefaultBranch() {
        this.defaultBranch = null;
    }
}
