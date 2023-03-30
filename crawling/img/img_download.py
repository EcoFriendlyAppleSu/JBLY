import threading
import time
from multiprocessing import Pool
from img import porterna_detailimg_download
import multiprocessing as mp
from multiprocessing import Process
from porterna_detailimg_download import porterna_img_downloader
from parsing.ProductTypes import product_types


if __name__ == '__main__':
    start_time = time.time()

    p_outwear = Process(target=porterna_img_downloader, args=("https://porterna.com/product/list.html?cate_no=541&page=", product_types.OUTWEAR.name))
    p_top = Process(target=porterna_img_downloader, args=("https://porterna.com/product/list.html?cate_no=789&page=", product_types.TOP.name))
    p_bottom = Process(target=porterna_img_downloader, args=("https://porterna.com/product/list.html?cate_no=28&page=", product_types.BOTTOM.name))
    p_acc = Process(target=porterna_img_downloader, args=("https://porterna.com/product/list.html?cate_no=44&page=", product_types.ACCESSORY.name))
    p_shoes = Process(target=porterna_img_downloader, args=("https://porterna.com/product/list.html?cate_no=79&page=", product_types.SHOES.name))

    p_outwear.start()
    p_top.start()
    p_bottom.start()
    p_acc.start()
    p_shoes.start()

    p_outwear.join()
    p_top.join()
    p_bottom.join()
    p_acc.join()
    p_shoes.join()

    print(time.time() - start_time)
