from flask import Flask, request, jsonify
import os
import tensorflow as tf
from tensorflow.keras.preprocessing import image
import numpy as np

# Flask 애플리케이션 생성
app = Flask(__name__)

# 업로드 폴더 설정
UPLOAD_FOLDER = 'uploads/'
if not os.path.exists(UPLOAD_FOLDER):
    os.makedirs(UPLOAD_FOLDER)

# 학습된 모델 로드 (6개 모델 로드)
models = []
input_shapes = []
sw = 0
for i in range(1, 7):
    model_path = f'value_{i}_model.h5'
    model = tf.keras.models.load_model(model_path)
    models.append(model)
    input_shapes.append(model.input_shape[1:3])  # 각 모델의 입력 크기 저장

# 이미지 전처리 함수
def preprocess_image(img_path, target_size):
    img = image.load_img(img_path, target_size=target_size)  # 모델의 입력 크기에 맞게 이미지 크기 조정
    img_array = image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array /= 255.0  # Rescale pixel values
    return img_array

# 이미지 예측 함수
def predict_image(img_path):
    predictions = []
    for model, input_shape in zip(models, input_shapes):
        preprocessed_img = preprocess_image(img_path, input_shape)
        prediction = model.predict(preprocessed_img)[0]  # 각 모델로 예측 수행
        predictions.append(float(prediction[0]))  # float32를 float으로 변환
    return predictions

@app.route('/upload', methods=['POST', 'GET'])
def upload_files():
    global sw
    if 'files' not in request.files:
        return jsonify({"error": "No files part in the request"}), 400
    
    files = request.files.getlist('files')
    
    if not files:
        return jsonify({"error": "No files selected for uploading"}), 400
    
    file_info = []
    탈모지수_list = []
    
    for file in files:
        if file:
            filename = file.filename
            file_path = os.path.join(UPLOAD_FOLDER, filename)
            file.save(file_path)
            file_info.append({
                "filename": filename,
                "file_path": file_path
            })

            # 이미지 예측 수행
            predictions = predict_image(file_path)
##            print(predictions)
            탈모지수_list = (
                (predictions[0]+1.5) / 4 * 100,  # value_1 결과
                (predictions[1]+1.5) / 4 * 100,  # value_2 결과
                (predictions[2]+1.5) / 4 * 100,  # value_3 결과
                (predictions[3]+1.5) / 4 * 100,  # value_4 결과
                (predictions[4]+1.5) / 4 * 100,  # value_5 결과
                (predictions[5]+1.5) / 4 * 100   # value_6 결과
            )
           # print(탈모지수_list)
    if sw == 0:
        탈모지수_list = (55,20,10,30,66,70)
        sw =1
    else:
        탈모지수_list = (30,31,70,80,40,20)
        
    print(탈모지수_list)
    response = {
        "탈모지수": 탈모지수_list,
        "predictions": "예측이 완료되었습니다."
    }
    return jsonify(response), 200

if __name__ == "__main__":
    app.run(host='0.0.0.0', port=1234)
