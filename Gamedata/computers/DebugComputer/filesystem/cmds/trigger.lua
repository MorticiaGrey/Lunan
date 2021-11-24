if args[1] == nil then
    print("usage: trigger [EVENT]")
    return
end
lunan.triggerEvent(args[1])
