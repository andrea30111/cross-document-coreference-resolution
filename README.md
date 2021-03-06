cross-document-coreference-resolution
=====================================
README

Jun Xie(xie@eecs.oregonstate.edu)

This is a cross document coreference resolution system written in java.  

Stanford coreference resolution system is a within document coreference resolution system. Hence, the essential data class is called Document. In the Document class, there are a lot of fields to represent the document information, such as gold ordered mentions by sentence, predicted orded mentions by sentence, predicted coreference clusters, gold coreference clusters, all predicted mentions, all gold mentions. Hence, in order to use stanford coreference resolution system, the very important task is to process each EECB file into a Document object. The example I am imitating is the stanford ACE 2005 machine reading sub-system. For the ACE 2005 corpus, there are two files related with one document, one is key.apf.xml and the other is raw.sgm. They constructed another class called AceDocument to perform the similar role as Document class. The AceDocument class is to represent the gold annotations, for example, AceEntityMention by sentences and AceEventMention by sentences, all AceEntityMention and all AceEventMention. Combined with the predicted mentions processed by Rulebased mention detection component, they changed the AcdDocument object to Document object. Based on the formed Document class, the system does coreference resolution. 

The overall architecture for EECB corpus is similar to their ACE 2005 machine reading sub-system. Due to the difference between EECB corpus and ACE corpus, the implementation is a bit different. The annotation is stored in a text file, called mentions.txt. Each line is represented as follows:

N or V? (0)  Topic(1)  Doc(2) Sentence Number(3) CorefID(4) StartIdx(5)  EndIdx(6) StartCharIdx(7)  EndCharIdx(8)

So I need to extract the event and entity mention according to the mentions.txt and the original source text, and represent the tokens, mentions, entity, event in my own built data structures for each topic, which consits of several documents (The reason for this is that our task is cross document coreference resolution). Based on those data strutures, I extract the gold annotations based on those data structures and predicted annotations according to the Rule based mention detection component provided by Stanford system. Then I need to adapt my EECBDocument class to Document class. Now, I am working and debugging on the transformation part. After the transformation part, then I can proceed to search part.  

In addition, the mentions.txt does not provide the semantic role annotations. I also need to import the annotations produced by this software(http://www.surdeanu.name/mihai/swirl/) into my code.
