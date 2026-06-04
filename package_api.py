import os
import zipfile
import shutil

def zip_directory(src_dir, dest_zip, exclude_dirs):
    with zipfile.ZipFile(dest_zip, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, dirs, files in os.walk(src_dir):
            # Prune directories in-place to exclude them from traversal
            dirs[:] = [d for d in dirs if d not in exclude_dirs]
            for file in files:
                file_path = os.path.join(root, file)
                rel_path = os.path.relpath(file_path, src_dir)
                zipf.write(file_path, rel_path)

if __name__ == '__main__':
    src = r"d:\Nalar\nalar-backend"
    dest1 = r"d:\Nalar\deliverables\Source_Code_API.zip"
    dest2 = r"d:\Nalar\deliverables\XI PPLG 3 - Kelompok Nalar - Nalar\2_Source_Code_dan_Build\2_Source_Code_API.zip"
    exclude = {'.git', 'node_modules'}
    
    # Ensure destination directories exist
    os.makedirs(os.path.dirname(dest1), exist_ok=True)
    os.makedirs(os.path.dirname(dest2), exist_ok=True)
    
    print("Zipping backend API source code...")
    zip_directory(src, dest1, exclude)
    
    # Copy the generated zip to the second destination
    shutil.copy2(dest1, dest2)
    
    size_kb1 = os.path.getsize(dest1) / 1024.0
    size_kb2 = os.path.getsize(dest2) / 1024.0
    
    print("SUCCESS: API source code packaged successfully!")
    print(f"Primary Zip:   {dest1} ({size_kb1:.2f} KB)")
    print(f"Group Folder:  {dest2} ({size_kb2:.2f} KB)")
