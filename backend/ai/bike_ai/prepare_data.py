import pandas as pd

# ---------- STEP 1: LOAD EXCEL ----------
file_path = "data/bikes.xlsx"   # make sure name matches exactly
df = pd.read_excel(file_path)

print("Total rows loaded:", len(df))


# ---------- STEP 2: CLEAN DATA ----------
df = df.dropna(how="all")
df = df.fillna("")

df["engine_cc"] = pd.to_numeric(df["engine_cc"], errors="coerce")
df["mileage_kmpl"] = pd.to_numeric(df["mileage_kmpl"], errors="coerce")
df["price_lakh"] = pd.to_numeric(df["price_lakh"], errors="coerce")

print("Rows after cleaning:", len(df))


# ---------- STEP 3: CREATE AI-READABLE TEXT ----------
bike_texts = []

for _, row in df.iterrows():
    text = (
        f"{row['brand']} {row['model']} is a {row['category']} motorcycle "
        f"released in {row['year']}. "
        f"It has a {row['engine_cc']} cc engine, "
        f"offers around {row['mileage_kmpl']} kmpl mileage, "
        f"and costs approximately {row['price_lakh']} lakh rupees. "
        f"This bike is suitable for {row['usage']}. "
        f"{row['description']}"
    )

    bike_texts.append({
        "bike_id": row["bike_id"],
        "text": text
    })


# ---------- STEP 4: SAVE FOR AI ----------
output_df = pd.DataFrame(bike_texts)
output_df.to_json("data/bike_ai_text.json", orient="records", indent=2)

print("✅ AI-readable bike data created successfully!")
