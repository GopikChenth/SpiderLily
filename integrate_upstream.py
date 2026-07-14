import os
import subprocess
import sys

def run_git_command(args):
    try:
        result = subprocess.run(["git"] + args, capture_output=True, text=True, check=True)
        return result.stdout.strip()
    except subprocess.CalledProcessError as e:
        print(f"Git command failed: {' '.join(e.cmd)}")
        if e.stdout:
            print(f"Stdout: {e.stdout}")
        if e.stderr:
            print(f"Stderr: {e.stderr}")
        return None

def find_unmerged_files():
    stdout = run_git_command(["status", "--porcelain"])
    if not stdout:
        return []
    
    unmerged = []
    for line in stdout.splitlines():
        if line.startswith("U") or "U" in line[:2]:
            status = line[:2]
            filepath = line[3:]
            unmerged.append((status, filepath))
    return unmerged

def fix_imports_and_packages(filepath):
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
    except Exception:
        try:
            with open(filepath, 'r', encoding='latin-1') as f:
                content = f.read()
        except Exception as e:
            print(f"Could not read {filepath}: {e}")
            return False

    old_package = "io.github.landwarderer.futon"
    new_package = "com.arcadelabs.spiderlily"
    
    if old_package in content:
        content = content.replace(old_package, new_package)
        try:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Updated packages/imports in: {filepath}")
            return True
        except Exception as e:
            print(f"Failed to write updates to {filepath}: {e}")
    return False

def resolve_unmerged():
    unmerged = find_unmerged_files()
    if not unmerged:
        print("No conflicts detected.")
        return
        
    print(f"Found {len(unmerged)} unmerged/conflicted paths. Resolving...")
    
    old_path_fragment = os.path.normpath("io/github/landwarderer/futon")
    new_path_fragment = os.path.normpath("com/arcadelabs/spiderlily")

    for status, filepath in unmerged:
        normalized_path = os.path.normpath(filepath)
        
        # 1. Handle file location conflicts (files added on upstream under the old directory path)
        if old_path_fragment in normalized_path:
            new_filepath = normalized_path.replace(old_path_fragment, new_path_fragment)
            print(f"\n[File Location Conflict] Detected file in old path: {normalized_path}")
            print(f"Moving to: {new_filepath}")
            
            # Create destination folder if needed
            os.makedirs(os.path.dirname(new_filepath), exist_ok=True)
            
            # Read from old, write to new with package name replaced
            try:
                with open(normalized_path, 'r', encoding='utf-8') as sf:
                    content = sf.read()
                content = content.replace("io.github.landwarderer.futon", "com.arcadelabs.spiderlily")
                with open(new_filepath, 'w', encoding='utf-8') as df:
                    df.write(content)
                
                # Delete old, add new to git
                if os.path.exists(normalized_path):
                    os.remove(normalized_path)
                run_git_command(["rm", "--cached", normalized_path])
                run_git_command(["add", new_filepath])
                print(f"Successfully migrated and staged: {new_filepath}")
            except Exception as e:
                print(f"Error migrating {normalized_path}: {e}")
                
        # 2. Handle files that are both modified (often package imports)
        elif status == "UU" or status == "AA":
            print(f"\n[Content Conflict] Checking {normalized_path}...")
            # If the conflict contains package mismatches, let's auto-replace them
            fix_imports_and_packages(normalized_path)
            print(f"Please inspect {normalized_path} to resolve any remaining logical conflicts, then run 'git add {normalized_path}'.")

def main():
    print("=== Futon Upstream Integration Tool ===")
    print("This script helps integrate upstream updates without package renaming conflicts.\n")
    
    # Check git status first
    status = run_git_command(["status", "--porcelain"])
    if status and not any(line.startswith("U") or "U" in line[:2] for line in status.splitlines()):
        print("Warning: You have uncommitted changes. Please commit or stash them first.")
        choice = input("Proceed anyway? (y/N): ").strip().lower()
        if choice != 'y':
            sys.exit(0)

    print("\nfetching latest updates from upstream...")
    run_git_command(["fetch", "upstream"])
    
    print("\nOptions:")
    print("1. Merge upstream/devel (full update)")
    print("2. Cherry-pick a specific commit")
    print("3. Only run conflict resolution helper (if merge/cherry-pick is already active)")
    print("4. Exit")
    
    option = input("Select an option (1-4): ").strip()
    
    if option == "1":
        print("Merging upstream/devel...")
        subprocess.run(["git", "merge", "upstream/devel"])
        resolve_unmerged()
    elif option == "2":
        commit = input("Enter the commit hash to cherry-pick: ").strip()
        if commit:
            print(f"Cherry-picking {commit}...")
            subprocess.run(["git", "cherry-pick", commit])
            resolve_unmerged()
    elif option == "3":
        resolve_unmerged()
    else:
        print("Exiting.")
        sys.exit(0)

    print("\n=== Integration Process Complete ===")
    print("Please check 'git status' and resolve any remaining conflicts manually.")

if __name__ == "__main__":
    main()
