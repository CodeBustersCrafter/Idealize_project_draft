package com.codebusters.idealizeprojectdraft.models

class DataShifter(private var dataList  :ArrayList<ItemModel>,private val size : Int) {
    private var endPointer : Int = 0
    fun getNextDataSet() : ArrayList<ItemModel>{
        var finalDataList = ArrayList<ItemModel>()
        if(endPointer==0){
            if(dataList.size>0){
                if(dataList.size<=size){
                    //add all
                    finalDataList = dataList
                }else{
                    while(finalDataList.size!=size){
                        finalDataList.add(dataList[endPointer])
                        endPointer++
                    }
                }
            }
        }else{
                if(dataList.size-endPointer<=size){
                    //add all
                    while(endPointer<=dataList.size-1){
                        finalDataList.add(dataList[endPointer])
                        endPointer++
                    }
                    endPointer=0
                }else{
                    while(finalDataList.size!=size){
                        finalDataList.add(dataList[endPointer])
                        endPointer++
                    }
                }
        }
        return finalDataList
    }
    public fun setData(data : ArrayList<ItemModel>){
        dataList=data
    }
}