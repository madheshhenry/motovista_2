import os
import xml.sax

def validate_xml_file(filepath):
    parser = xml.sax.make_parser()
    try:
        parser.parse(filepath)
        return True, None
    except xml.sax.SAXParseException as e:
        return False, f"{e.getLineNumber()}:{e.getColumnNumber()} - {e.getMessage()}"
    except Exception as e:
        return False, str(e)

def scan_directory(directory):
    malformed_files = []
    print(f"Scanning directory: {directory}")
    if not os.path.exists(directory):
        print(f"ERROR: Directory not found: {directory}")
        return

    count = 0
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".xml"):
                count += 1
    print(f"Found {count} XML files to scan.")
    
    for root, _, files in os.walk(directory):
        for file in files:
            if file.endswith(".xml"):
                full_path = os.path.join(root, file)
                is_valid, error = validate_xml_file(full_path)
                if not is_valid:
                    # We are specifically looking for "content must be well-formed" which usually means junk at end
                    if "well-formed" in error or "content" in error:
                         malformed_files.append((full_path, error))
                         print(f"[FAIL] {file}: {error}")

    with open("results.txt", "w", encoding="utf-8") as f:
        f.write(f"Scanned {count} XML files.\n")
        if not malformed_files:
            f.write("All files seem valid.\n")
        else:
            f.write(f"Found {len(malformed_files)} malformed files:\n")
            for path, error in malformed_files:
                f.write(f"{path} : {error}\n")
    print("Done writing to results.txt")

if __name__ == "__main__":
    scan_directory(r"D:\motovista_deep\app\src\main\res\layout")
