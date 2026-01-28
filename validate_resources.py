import os
import re
import xml.etree.ElementTree as ET

project_res_path = r'd:\motovista_deep\app\src\main\res'
layout_path = os.path.join(project_res_path, 'layout')
drawable_path = os.path.join(project_res_path, 'drawable')
color_path = os.path.join(project_res_path, 'values', 'colors.xml')
color_dir_path = os.path.join(project_res_path, 'color')
anim_path = os.path.join(project_res_path, 'anim')
font_path = os.path.join(project_res_path, 'font')

def get_existing_resources():
    drawables = {os.path.splitext(f)[0] for f in os.listdir(drawable_path)}
    layouts = {os.path.splitext(f)[0] for f in os.listdir(layout_path)}
    anims = set()
    if os.path.exists(anim_path):
        anims = {os.path.splitext(f)[0] for f in os.listdir(anim_path)}
    fonts = set()
    if os.path.exists(font_path):
        fonts = {os.path.splitext(f)[0] for f in os.listdir(font_path)}
    
    colors = set()
    if os.path.exists(color_path):
        tree = ET.parse(color_path)
        root = tree.getroot()
        for color in root.findall('color'):
            colors.add(color.get('name'))
    if os.path.exists(color_dir_path):
        for f in os.listdir(color_dir_path):
            colors.add(os.path.splitext(f)[0])
            
    return drawables, layouts, anims, fonts, colors

def scan_layouts(drawables, layouts, anims, fonts, colors):
    missing_drawables = set()
    missing_layouts = set()
    missing_anims = set()
    missing_fonts = set()
    missing_colors = set()
    
    # Common Android/Material drawables to ignore
    ignored_res = ['selectableItemBackground', 'selectableItemBackgroundBorderless', 'transparent']

    for filename in os.listdir(layout_path):
        if not filename.endswith('.xml'): continue
        path = os.path.join(layout_path, filename)
        try:
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
                
                # Find all @something/references
                refs = re.findall(r'@(drawable|layout|anim|font|color)/([a-zA-Z0-9_]+)', content)
                for res_type, res_name in refs:
                    if res_name in ignored_res: continue
                    
                    if res_type == 'drawable':
                        if res_name not in drawables:
                            missing_drawables.add((filename, res_name))
                    elif res_type == 'layout':
                        if res_name not in layouts:
                            missing_layouts.add((filename, res_name))
                    elif res_type == 'anim':
                        if res_name not in anims:
                            missing_anims.add((filename, res_name))
                    elif res_type == 'font':
                        if res_name not in fonts:
                            missing_fonts.add((filename, res_name))
                    elif res_type == 'color':
                        if res_name not in colors:
                            missing_colors.add((filename, res_name))
        except Exception as e:
            pass
                        
    return missing_drawables, missing_layouts, missing_anims, missing_fonts, missing_colors

try:
    d, l, a, f, c = get_existing_resources()
    md, ml, ma, mf, mc = scan_layouts(d, l, a, f, c)

    with open('d:/motovista_deep/scan_results.txt', 'w') as out:
        out.write("MISSING DRAWABLES:\n")
        for l, r in sorted(md): out.write(f"  In {l}: @drawable/{r}\n")

        out.write("\nMISSING LAYOUTS:\n")
        for l, r in sorted(ml): out.write(f"  In {l}: @layout/{r}\n")

        out.write("\nMISSING ANIMS:\n")
        for l, r in sorted(ma): out.write(f"  In {l}: @anim/{r}\n")

        out.write("\nMISSING COLORS:\n")
        for l, r in sorted(mc): out.write(f"  In {l}: @color/{r}\n")

        out.write("\nSCAN COMPLETE\n")
except Exception as e:
    with open('d:/motovista_deep/scan_results.txt', 'w') as out:
        out.write(f"EROR: {str(e)}")
